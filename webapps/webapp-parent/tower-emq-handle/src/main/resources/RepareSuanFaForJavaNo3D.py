# -*- coding: utf-8 -*

import json
import math
import os
import sys

import numpy as np
from scipy.spatial import ConvexHull
from scipy.spatial import Delaunay
from sklearn.neighbors import LocalOutlierFactor


def sort_json_by_key(json_str):
    # 将JSON数据转换为Python字典对象
    data = json.loads(json_str)

    # 按一级键对字典进行排序
    sorted_data = dict(sorted(data.items()))

    # 对每个一级键的二级键按从小到大排序
    for key in sorted_data:
        sorted_data[key] = dict(sorted(sorted_data[key].items()))

    # 将排序后的字典转换为JSON格式并返回
    sorted_json_str = json.dumps(sorted_data, indent=2)
    return sorted_json_str



def convert_to_pointcloud(json_str,jsonIF):
    if(jsonIF):
        obj = json_str
    else:
        sorted_json_str = sort_json_by_key(json_str)
        obj = json.loads(sorted_json_str)
    points = []
    disTotal = 0
    for levelAngleStr, vertical in obj.items():
        levelAngle = int(levelAngleStr)
        for angStr, disStr in vertical.items():
            ang = int(angStr) / 100
            # if (levelAngle == 81):
            #     ang = ang + 10

            dis = int(disStr)
            disTotal += dis
            if(dis >= 180000):
                continue

            z = math.cos(math.radians(ang)) * dis
            temp = math.sin(math.radians(ang)) * dis
            x = math.cos(math.radians(levelAngle)) * temp
            y = math.sin(math.radians(levelAngle)) * temp
            points.append([x, y, z])
    pointcloud = np.array(points)
    print("点云所有点距离之和："+str(disTotal))
    # 获取数组形状并计算点云总数
    num_points = pointcloud.shape[0]
    print("原始点云总数为：", num_points)
    # print("优化后点云总数为：", num_points)
    return pointcloud

#把json的角度距离转为极坐标
def convert_to_pointcloudNew(json_str,jsonIF):
    if(jsonIF):
        obj = json_str
    else:
        sorted_json_str = sort_json_by_key(json_str)
        obj = json.loads(sorted_json_str)
    points = []
    disTotal = 0

    removeTotal = 0
    for levelAngleStr, vertical in obj.items():
        pointsTemp = []
        point1 = [0, 0, 0]
        point2 = [0, 0, 0]
        levelAngle = int(levelAngleStr)
        for angStr, disStr in vertical.items():
            # print("angStr: "+angStr)
            ang = int(angStr) / 100
            # print("ang: " + str(ang))
            dis = int(disStr)
            disTotal += dis
            if(dis >= 180000):
                continue
            z = math.cos(math.radians(ang)) * dis
            temp = math.sin(math.radians(ang)) * dis
            x = math.cos(math.radians(levelAngle)) * temp
            y = math.sin(math.radians(levelAngle)) * temp
            # points.append([x, y, z])
            pointsTemp.append([x, y, z])

            if (ang == 120):
                point1 = [x, y, z]
            # point1 = [0, 0, 0]
            if(ang == 240):
                point2 = [x, y, z]

            # if (ang == 159.9):
            #     point1 = [x, y, z]
            # # point1 = [0, 0, 0]
            # if(ang == 200.1):
            #     point2 = [x, y, z]


        # angle_x, angle_y, angle_z = calculate_angle(point1, point2)

        result = calculate_angle(point1, point2)
        if result is not None:
            angle_x, angle_y, angle_z = result
            # 继续处理角度值
        else:
            print("水平角度: {}".format(str(levelAngle)))
            print("垂直角度: {}".format(str(ang)))
            print("该线条无效已去除!")
            removeTotal += 1
            continue
        # 打印夹角信息
        # print(f"与 x 轴的夹角：{angle_x} 度")
        # print(f"与 x 轴的夹角：{angle_x1} 度")
        # print(f"与 y 轴的夹角：{angle_y} 度")
        # print(f"与 y 轴的夹角：{angle_y1} 度")
        # print(f"与 z 轴的夹角：{angle_z} 度")
        # print(f"与 z 轴的夹角：{angle_z1} 度")

        pointcloud = np.array(pointsTemp)
        #计算补偿角度
        need1 = 0
        if(angle_z<0):
            need1 = (90+angle_z)
            # need1 = 0

        elif (angle_z > 0 ):
            need1 = (90 - angle_z)
            # need1 = 0
        # print(need1)
        # 角度差过大直接舍弃
        if (abs(need1) > 25):
            removeTotal += 1
            continue
        # need2 = angle_y - 90
        # need3 = angle_z - 180
        for angStr, disStr in vertical.items():
            # print((int(angStr) / 100))
            ang = (int(angStr) / 100) + need1
            # print(ang)
            dis = int(disStr)
            disTotal += dis
            if (dis >= 180000):
                continue
            z = math.cos(math.radians(ang)) * dis
            temp = math.sin(math.radians(ang)) * dis
            x = math.cos(math.radians(levelAngle)) * temp
            y = math.sin(math.radians(levelAngle)) * temp
            points.append([x, y, z])

    if (removeTotal >3):
        raise ValueError("无效数据"+str(removeTotal) +"过多,本次测量无效!")
    pointcloud = np.array(points)
    print("点云所有点距离之和："+str(disTotal))
    # 获取数组形状并计算点云总数
    num_points = pointcloud.shape[0]
    print("原始点云总数为：", num_points)
    # np.savetxt('D:/算法/点云文件/liaotaMoXing.txt', points)
    return pointcloud



def calculate_angle(p1, p2):
    # Calculate the vector between the two points
    vector = [p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]]
    vector_length = math.sqrt(vector[0] ** 2 + vector[1] ** 2 + vector[2] ** 2)

    if vector_length == 0:
        # Vector has zero length, handle this case accordingly
        if p1 == p2:
            print("两个点坐标相同!")
        else:
            if p1[0] == p2[0] and p1[1] != p2[1] and p1[2] != p2[2]:
                print("两个点x相同,yz不同")
            else:
                print("无法计算")
        return None  # or any other specific value indicating invalid angles

    # Calculate the angle between the vector and the x-axis
    angle_x = math.degrees(math.acos(vector[0] / vector_length))
    if vector_length > 0 and vector[1] < 0:
        angle_x = -angle_x

    # Calculate the angle between the vector and the y-axis
    angle_y = math.degrees(math.acos(vector[1] / vector_length))
    if vector_length > 0 and vector[0] < 0:
        angle_y = -angle_y

    # Calculate the angle between the vector and the z-axis
    angle_z = math.degrees(math.acos(vector[2] / vector_length))
    if vector_length > 0 and vector[0] < 0:
        angle_z = -angle_z

    return angle_x, angle_y, angle_z





# 判断是否是json
def is_valid_json(str):
    try:
        json.loads(str)
        return True
    except json.JSONDecodeError:
        return False


# 读取 IGS 文件
def read_igs_file(filename):
    IGS_file = open(filename, 'r')
    IGS_lines = IGS_file.readlines()
    temp2 = []
    # print(IGS_lines)
    for line in IGS_lines:
        if line[:4] == "116,":
            temp1 = line.split(',')
            temp2.append(temp1[1:4])

    L = len(temp2)
    point = np.zeros((L, 3), np.float32)
    for i in range(len(temp2)):
        point[i, 0] = float(temp2[i][0])
        point[i, 1] = float(temp2[i][1])
        try:
            point[i, 2] = float(temp2[i][2])
        except ValueError:
            point[i, 2] = 0.0  # 或其他您认为适合的默认值
        # point[i, 2] = float(temp2[i][2])

    print(point)
    # np.savetxt('D:/算法/点云文件/wgz.txt', point)
    return np.array(point)

def read_igs_file(filename):
    IGS_file = open(filename, 'r')
    IGS_lines = IGS_file.readlines()
    temp2 = []
    # print(IGS_lines)
    for line in IGS_lines:
        if line[:4] == "116,":
            temp1 = line.split(',')
            temp2.append(temp1[1:4])

    L = len(temp2)
    point = np.zeros((L, 3), np.float32)
    for i in range(len(temp2)):
        point[i, 0] = float(temp2[i][0])
        point[i, 1] = float(temp2[i][1])
        try:
            point[i, 2] = float(temp2[i][2])
        except ValueError:
            point[i, 2] = 0.0  # 或其他您认为适合的默认值
        # point[i, 2] = float(temp2[i][2])

    print(point)
    # np.savetxt('D:/算法/点云文件/wgz.txt', point)
    return np.array(point)

def read_json_file(file_path):
    with open(file_path, "r") as f:
        json_str = json.load(f)
    return json_str



def is_none(obj):
    return obj is None


def calculate_volume(repaired_point_cloud):
    # 使用Delaunay三角剖分计算体积
    delaunay = Delaunay(repaired_point_cloud)
    tetrahedrons = delaunay.points[delaunay.simplices]
    volume = 0
    for tetrahedron in tetrahedrons:
        v = np.abs(np.linalg.det(tetrahedron[1:] - tetrahedron[0])) / 6
        volume += v
    # return repaired_point_cloud, volume
    return volume




def remove_outliers(point_cloud):
    # 获取点云数据的坐标
    coordinates = point_cloud[:, :3]

    # 计算料塔的中心轴
    z_coords = coordinates[:, 2]
    z_min = np.min(z_coords)
    z_max = np.max(z_coords)
    z_axis = (z_min + z_max) / 2.0

    # 划分料塔为轴对称图形
    symmetric_points = []
    for point in coordinates:
        if point[2] >= z_axis:
            symmetric_points.append(point)

    symmetric_points = np.array(symmetric_points)

    # 去除离群点
    while True:
        # 计算每个中心点的水平位置上点到中心点的距离
        distances = np.linalg.norm(symmetric_points[:, :2] - symmetric_points[:, :2].mean(axis=0), axis=1)
        mean_distance = np.mean(distances)
        std_distance = np.std(distances)

        # 判断离群点并去除
        outliers = np.where(distances > mean_distance + 2 * std_distance)[0]
        if len(outliers) == 0:
            break

        symmetric_points = np.delete(symmetric_points, outliers, axis=0)

    # 输出最终的点云数据
    processed_point_cloud = np.vstack((symmetric_points, point_cloud[np.where(point_cloud[:, 2] < z_axis)]))

    return processed_point_cloud


def remove_outliers(point_cloud, neighbors=20, contamination=0.1):
    """
    通过 LOF 方法去除点云中的离群点

    参数：
    point_cloud: numpy 数组，表示点云，每个点是一个坐标向量
    neighbors: int，表示用于计算 LOF 的邻近点的数量，默认为 20
    contamination: float，表示预期的离群点比例，默认为 0.1

    返回：
    清理后的点云，numpy 数组
    """

    # 使用 LOF 方法计算离群因子
    clf = LocalOutlierFactor(n_neighbors=neighbors, contamination=contamination)
    outlier_scores = clf.fit_predict(point_cloud)

    # 根据离群因子的结果进行过滤
    filtered_cloud = point_cloud[outlier_scores > 0]

    return filtered_cloud




if __name__ == "__main__":
    if len(sys.argv) < 4:
        print("请提供至少3个参数：1. JSON 参数或文件路径 2. 方法名称 3. 输出文件名")
        sys.exit(1)

    input_str = sys.argv[1]
    method_name = sys.argv[2]
    output_filename = sys.argv[3]

    points = []
    repaired_points = []
    fix = False
    # 等待输入数据
    if not input_str:
        print("错误的input_str!")
    if is_valid_json(input_str):
        try:
            # 将角度和距离转换成极坐标
            # points = convert_to_pointcloud(input_str,False)
            points = convert_to_pointcloudNew(input_str, False)
            # 两次过滤无效点
            points = remove_outliers(points)
            points = remove_outliers(points)
            youXiaoPointCloud = np.array(points)
            youXiaoPoint = youXiaoPointCloud.shape[0]
            print("过滤后有效点云数：", youXiaoPoint)
        except ValueError:
            print("json格式错误!")
    else:
        basename, ext = os.path.splitext(input_str)
        if ext == ".txt":
            # 读取txt文件中的数据
            data = np.loadtxt(input_str)
            # 将数据转换成三维数组
            points = np.reshape(data, (-1, 3))
        elif ext == ".igs":
            # 假设点云数据存在numpy数组points中
            points = read_igs_file(input_str)
        elif ext == ".json":
            # 假设点云数据存在numpy数组points中
            json_str = read_json_file(input_str)
            points = convert_to_pointcloudNew(json_str, True)
            # 两次过滤无效点
            points = remove_outliers(points)
            points = remove_outliers(points)
            youXiaoPointCloud = np.array(points)
            youXiaoPoint = youXiaoPointCloud.shape[0]
            print("过滤后有效点云数：", youXiaoPoint)
        else:
            print('无效文件!')

    # 根据指定方法执行相应的操作
    if method_name == '1':
        fix = True
        hull = ConvexHull(points)
        repaired_points = points[hull.vertices]
        value = calculate_volume(repaired_points)

    else:
        print('无效的方法!')

    if is_none(value):
        print("无法计算体积!")
    else:
        print("[立方毫米]点云围成的封闭体积为：", value)
        cubic_meters = round(value / 1000000000, 4)
        final = cubic_meters * 0.9
        print("[补偿后]点云围成的封闭体积为：" + str(final) + "立方米")
        final = final * 1000000000
        print("[补偿后]点云围成的封闭体积为：" + str(final) + "立方毫米")
        # final = cubic_meters * 0.9
        # final = final * 1000000000
        print("Volume:", final)

