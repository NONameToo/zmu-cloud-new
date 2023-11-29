package com.zmu.cloud.commons.constants;

public class Constants {

//    public static final String JX_APP_BASE_URL = "http://49.4.1.109:8080/app";
//    public static final String JX_APP_BASE_URL = "https://test.juxingnongmu.cn/app";//测试环境
//    public static final String JX_APP_BASE_URL = "https://erp.juxingnongmu.cn/app";//生产环境
//    public static final String JX_APP_BASE_URL = "http://localhost:8080/app";
    public static final String LOGIN = "/login";

    /************************************************ 用户管理 Begin ***********************************************/
    public static final String getZCList = "/getZCList"; //用户养殖场
    public static final String saveZC = "/saveZC"; //切换保存
    /************************************************ 用户管理 End ***********************************************/

    /************************************************ 繁殖管理 Begin ***********************************************/
    public static final String getWeeks = "/getWeeks"; //繁殖管理列表查询
    public static final String getBreedInfos_New = "/getBreedInfos_New"; //繁殖管理列表明细查询

    public static final String referOrUnReferRecord = "/ReferOrUnReferRecord"; //提交、反提交
    public static final String deleteRecord = "/DeleteRecord";//删除

    public static final String getGatherOne = "/getGatherOne"; //采精猪只选择
    public static final String saveGather = "/saveGather";//保存采精
    public static final String updateGather = "/updateGather";//编辑采精
    public static final String referGather = "/referGather";//采精提交
    public static final String unReferGather = "/unReferGather";//采精反提交
    public static final String deleteGather = "/deleteGather";//采精删除

    public static final String getOestrusRecordZOneNo = "/OestrusRecode/getOestrusRecordZOneNo"; //发情猪只选择
    public static final String save_oestrus_record = "/OestrusRecode/saveOestrusRecord";//保存发情
    public static final String updateOestrusRecord = "/OestrusRecode/updateOestrusRecord";//编辑发情
    public static final String referOestrusRecord = "/OestrusRecode/referOestrusRecord";//发情提交、反提交
    public static final String deleteOestrusRecord = "/OestrusRecode/deleteOestrusRecord";//发情删除

    public static final String breedInfoByKey = "/breedInfoByKey"; //配种猪只选择
    public static final String verfiyPigType = "/verfiyPigType"; //配种后备猪只选择
    public static final String addBreed = "/AddBreed";//保存配种
    public static final String updateBreed = "/UpdateBreed";//编辑配种

    public static final String checkInfoByKey = "/checkInfoByKey"; //妊检猪只选择
    public static final String addCheck = "/AddCheck";//保存妊检
    public static final String updateCheck = "/UpdateCheck";//编辑妊检

    public static final String birthInfoByKey = "/birthInfoByKey"; //分娩猪只选择
    public static final String addBirth = "/AddBirth";//保存分娩
    public static final String updateBirth = "/UpdateBirth";//编辑分娩

    public static final String ablactationInfoByKey = "/ablactationInfoByKey"; //断奶猪只选择
    public static final String addAblactation = "/AddAblactation";//保存断奶
    public static final String updateAblactation = "/UpdateAblactation";//编辑断奶

    /************************************************ 繁殖管理 End ***********************************************/
    /************************************************ 死亡管理 Begin ***********************************************/

    public static final String dieRecordUnrefer = "/dieRecordUnrefer"; //种猪、批次死亡反提交
    public static final String dieRecordRefer = "/dieRecordRefer"; //种猪、批次死亡提交
    public static final String unReferBackupDie = "/AppDieBackupController/unReferBackupDie"; //后备死亡反提交
    public static final String referBackupDie = "/AppDieBackupController/referBackupDie"; //后备死亡提交

    public static final String saveBoarDie = "/saveBoarDie"; //保存种猪死亡
    public static final String saveBackupDie = "/AppDieBackupController/saveBackupDie"; //保存后备死亡
    public static final String saveBatchDie = "/saveBatchDie"; //保存批次死亡

    public static final String updateBoarDie = "/updateBoarDie"; //编辑种猪死亡
    public static final String updateBackupElimi = "/AppDieBackupController/updateBackupElimi"; //编辑后备死亡
    public static final String updateBatchDie = "/updateBatchDie"; //编辑批次死亡

    public static final String dieRecordDelete = "/dieRecordDelete"; //删除种猪、批次死亡
    public static final String deleteBackupDie = "/AppDieBackupController/deleteBackupDie"; //删除后备死亡

    public static final String getGroupBoarNo = "/getGroupBoarNo"; //种猪死亡猪只选择
    public static final String getElimiBackupNo = "/AppElimiBackupController/getElimiBackupNo"; //种猪后备猪只选择

    public static final String boarDieSearchInfo = "/boarDieSearchInfo"; //种猪死亡列表
    public static final String backupDieSearchInfo = "/AppDieBackupController/backupDieSearchInfo"; //后备死亡列表
    public static final String batchDieSearchInfo = "/batchDieSearchInfo"; //批次死亡列表


    /************************************************ 死亡管理 End ***********************************************/

    public static final String getBoarPigInfo = "/getBoarPigInfo"; //公猪选择
    public static final String getBatchInfos = "/getBatchInfos"; //根据舍栏ID，获取批次信息

    /************************************************ 预警信息 Begin ***********************************************/
    public static final String warnInfo = "/warnInfo"; //预警信息
    public static final String warnInfoDt = "/warnInfoDt"; //预警信息详情
    /************************************************ 预警信息 End ***********************************************/


    /************************************************ 转群 Begin ***********************************************/
    public static final String saveGroup = "/saveGroup"; //种猪新增
    public static final String saveBatch = "/saveBatch"; //批次新增
    public static final String saveBackupGroup = "/AppGroupBackupController/saveBackupGroup"; //后备新增

    public static final String unReferBoarGroup = "/unReferBoarGroup"; //种猪反提交
    public static final String unReferBatchGroup = "/unReferBatchGroup"; //批次反提交
    public static final String unReferBackupGroup = "/AppGroupBackupController/unReferBackupGroup"; //后备反提交

    public static final String updateBoarGroup = "/updateBoarGroup"; //种猪编辑
    public static final String updateBatchGroup = "/updateBatchGroup"; //批次编辑
    public static final String updateBackupGroup = "/AppGroupBackupController/updateBackupGroup"; //后备编辑

    public static final String deleteBoarGroup = "/deleteBoarGroup"; //种猪删除
    public static final String deleteBatchGroup = "/deleteBatchGroup"; //批次删除
    public static final String deleteBackupGroup = "/AppGroupBackupController/deleteBackupGroup"; //后备删除

    public static final String referBoarGroup = "/referBoarGroup"; //种猪提交
    public static final String referBatchGroup = "/referBatchGroup"; //批次提交
    public static final String referBackupGroup = "/AppGroupBackupController/referBackupGroup"; //后备提交

    public static final String getGroupBoarInfo = "/getGroupBoarInfo"; //种猪查询
    public static final String getGroupBatchInfo = "/getGroupBatchInfo"; //批次查询
    public static final String getGroupBackupInfo = "/AppGroupBackupController/getGroupBackupInfo"; //后备查询

    public static final String getGroupBoarLov = "/getGroupBoarLov"; //种猪转群流程
    public static final String getGroupFlow = "/getGroupFlow"; //批次、后备转群流程
    /************************************************ 转群 End ***********************************************/

    public static final String uploaddieFj = "/AppDieBackupController/uploaddieFj"; //图片上传
    public static final String getBatchLov = "/getBatchLov"; //转入转出批次编号接口
    public static final String backupElimiBatchZC = "/AppElimiBackupController/BackupElimiBatchZC"; //获取淘汰批次编号
    public static final String backupElimiBatchZR = "/AppElimiBackupController/BackupElimiBatchZR"; //获取转入批次编号
    public static final String deleteDieFj = "/deleteDieFj"; //删除图片


    public static final String querypz = "/querypz"; //查询配种员
    public static final String getZcPregnantDays = "/getZcPregnantDays"; //标识怀孕天数接口


    public static final String saveBackfat  = "/saveBackfat"; //保存背膘记录
    public static final String sowDocument  = "/sowDocument"; //档案
    public static final String getArchivesInfo  = "/getArchivesInfo "; //母猪档案详情
    public static final String getBoarArchivesInfo  = "/getBoarArchivesInfo"; //公猪档案详情


    public static final String sowPigCount  = "/sowPigCount"; //种母猪存栏
    public static final String getSowPigCount_Breed  = "/getSowPigCount_Breed"; //种猪信息—品种、舍栏
    public static final String meatPigCount  = "/meatPigCount"; //肉猪存栏

    public static final String batchQueryDie  = "/batch/queryDie"; //死亡记录审核查询
    public static final String batchUpdateLeadAudit  = "/batch/updateLeadAudit"; //死亡记录领导审核接口
    public static final String batchQueryBatch  = "/batch/queryBatch"; //批次查询接口
    public static final String batchGetBatchInfoById  = "/batch/getBatchInfoById"; //通过id查询批次信息
    public static final String batchSaveBatch = "/batch/saveBatch"; //新增批次
    public static final String batchUpdateBatch = "/batch/updateBatch"; //修改批次

    public static final String queryStockLineRecord = "/stockLine/queryStockLineRecord"; //查询料线喂料记录
    public static final String getStockLineRecordById = "/stockLine/getStockLineRecordById"; //通过id查询料线喂料记录
    public static final String saveStockLineRecord = "/stockLine/saveStockLineRecord"; //新增料线喂料记录
    public static final String updateStockLineRecord = "/stockLine/updateStockLineRecord"; //更新料线喂料记录
    public static final String deleteStockLine = "/stockLine/deleteFeedStockline"; //通过id删除料线喂料记录
    public static final String queryStockLine = "/stockLine/queryStockLine"; //查询料线信息
    public static final String queryFodder = "/stockLine/queryFodder"; //获取当前料线可用饲料
    public static final String queryStock = "/stockLine/queryStock"; //获取存栏
    public static final String setRefer = "/stockLine/setRefer"; //提交反提交料线喂料记录

    public static final String getEpidemicInfo = "/getEpidemicInfo"; //批次免疫记录
    public static final String unReferEpidemic = "/unReferEpidemic"; //提交/反提交
    public static final String deleteEpidemic = "/deleteEpidemic"; //删除
    public static final String saveEpidemicGroup = "/saveEpidemicGroup"; //新增
    public static final String updateEpidemicGroup = "/updateEpidemicGroup"; //修改
    public static final String upLoadEpidemicPic = "/upLoadEpidemicPic"; //上传图片
    public static final String deleteEpidemicPic = "/deleteEpidemicPic"; //删除图片
    public static final String getMYBatch = "/getMYBatch"; //批次编号
    public static final String getImmps = "/getImmps"; //项目/疫苗名称



    public static final String getScanId = "/getScanId"; //二维码扫描
    public static final String breedInfoById = "/breedInfoById"; //二维码扫描
    public static final String checkInfoById = "/checkInfoById"; //二维码扫描
    public static final String birthInfoById = "/birthInfoById"; //二维码扫描
    public static final String ablactationInfoById = "/ablactationInfoById"; //二维码扫描
    public static final String scanOestrusRecode = "/OestrusRecode/scanOestrusRecode"; //二维码扫描


    public static final String getmyDormInfo = "/yhgl/YhAdminAppController/getmyDormInfo"; //我的养户
    public static final String getDormInfo = "/yhgl/YhAdminAppController/getDormInfo"; //我的全部养户
    public static final String deleteyhpicture = "/YHDieBatchContrl/zq_batch/deleteyhpicture"; //养户删除图片
    public static final String sendMapLocation = "/YHDieBatchContrl/zq_batch/sendMapLocation"; //养户上传地理坐标
    public static final String uploadyhPicture = "/YHDieBatchContrl/zq_batch/uploadyhPicture"; //养户上传图片
    public static final String yhDieDetailSearchInfo = "/yhgl/YhAdminAppController/yhDieDetailSearchInfo"; //养户猪只死亡查询
    public static final String referYhDie = "/yhgl/YhAdminAppController/referYhDie"; //养户提交死亡记录
    public static final String unReferYhDie = "/yhgl/YhAdminAppController/unReferYhDie"; //养户反提交提交死亡记录
    public static final String deleteYhDie = "/yhgl/YhAdminAppController/deleteYhDie"; //养户删除死亡记录
    public static final String saveYhDie = "/yhgl/YhAdminAppController/saveYhDie"; //养户新增死亡记录
    public static final String updateYhDie = "/yhgl/YhAdminAppController/updateYhDie"; //养户修改死亡记录

    public static final String getDormIdk = "/yhgl/YhAdminAppController/getDormIdk"; // 获取养户管理员所管理的养户id（巨星oa里面点击底部我的养户时调用）
    public static final String getDieCauseTypeDy = "/getDieCauseTypeDy"; //死亡类型和死亡原因
    public static final String getDieMode = "/getDieMode"; //处理方式
    public static final String getDormInfodie = "/yhgl/YhAdminAppController/getDormInfodie"; //死亡记录-我的养户
    public static final String yhDeleteDieFj = "/YHDieBatchContrl/zq_batch/deleteDieFj"; //猪只死亡删除图片
    public static final String yhUploaddieFj = "/YHDieBatchContrl/zq_batch/uploaddieFj"; //猪只死亡上传图片
    public static final String getPigStatus = "/getPigStatus"; //猪只死亡上传图片


    public static final String boarGroupInfoAddById = "/boarGroupInfoAddById"; //转群、死亡扫码
    public static final String breedInfoAddById = "/breedInfoAddById"; //配种扫码
    public static final String checkInfoAddById = "/checkInfoAddById"; //妊检信息扫描新增
    public static final String birthInfoAddById = "/birthInfoAddById"; //分娩信息扫描新增
    public static final String ablactationInfoAddById = "/ablactationInfoAddById"; //断奶信息扫描新增


//##################################################生物安全#############################################################
    public static final String common_findDept = "/common/findDept";
    public static final String common_findDetectionLab = "/common/findDetectionLab";
    public static final String common_findYh = "/common/findYh";
    public static final String common_findCompany = "/common/findCompany";
    public static final String common_sampleInfo = "/common/sampleInfo";
    public static final String common_checkItem = "/common/checkItem";
    public static final String common_point = "/common/point";
    public static final String common_personal = "/common/personal";
    public static final String common_carNo = "/common/carNo";
    public static final String common_evnTemplate = "/common/evnTemplate";
    public static final String common_evnPoint = "/common/evnPoint";
    public static final String common_oneNo = "/common/oneNo";
    public static final String common_findYhByInfo = "/common/findYhByInfo";
    public static final String common_findPerson = "/common/findPerson";
    public static final String common_selectJcNo = "/common/selectJcNo";
    public static final String common_enterPerson = "/common/enterPerson";
    public static final String common_findIsolationCenter = "/common/findIsolationCenter";
    public static final String common_gainCarNo = "/common/gainCarNo";
    public static final String common_gainCarNoCarData = "/common/gainCarNoCarData";

    public static final String clgl_upLoadPic = "/clgl/upLoadPic"; //图片上传
    public static final String clgl_deletePic = "/clgl/deletePic"; //删除图片

    public static final String clgl_query = "/clgl/query";
    public static final String clgl_saveOrUpdateDoc = "/clgl/saveOrUpdateDoc";
    public static final String clgl_findOne = "/clgl/findOne";
    public static final String clgl_updateZtag = "/clgl/updateZtag";
//    public static final String clgl_gainCarNo = "/clgl/gainCarNo";

    public static final String clglqx_query = "/clglqx/query";
    public static final String clglqx_saveOrUpdateDoc = "/clglqx/saveOrUpdateDoc";
    public static final String clglqx_findOne = "/clglqx/findOne";
    public static final String clglqx_referOrUnrefer = "/clglqx/referOrUnrefer";
    public static final String clglqx_gainSterilizeCenter = "/clglqx/gainSterilizeCenter";

    public static final String clgljc_query = "/clgljc/query";
    public static final String clgljc_saveOrUpdateDoc = "/clgljc/saveOrUpdateDoc";
    public static final String clgljc_findOne = "/clgljc/findOne";
    public static final String clgljc_referOrUnrefer = "/clgljc/referOrUnrefer";
    public static final String clgljc_gainDetection = "/clgljc/gainDetection";

    public static final String clglxd_query = "/clglxd/query";
    public static final String clglxd_saveOrUpdateDoc = "/clglxd/saveOrUpdateDoc";
    public static final String clglxd_findOne = "/clglxd/findOne";
    public static final String clglxd_referOrUnrefer = "/clglxd/referOrUnrefer";

    public static final String personInspection_query = "/personInspection/query";
    public static final String personInspection_saveOrUpdateDoc = "/personInspection/saveOrUpdateDoc";
    public static final String personInspection_deleteDoc = "/personInspection/deleteDoc";
    public static final String personInspection_findOne = "/personInspection/findOne";
    public static final String personInspection_referOrUnrefer = "/personInspection/referOrUnrefer";

    public static final String envInspection_query = "/envInspection/query";
    public static final String envInspection_saveOrUpdateDoc = "/envInspection/saveOrUpdateDoc";
    public static final String envInspection_deleteDoc = "/envInspection/deleteDoc";
    public static final String envInspection_findOne = "/envInspection/findOne";
    public static final String envInspection_referOrUnrefer = "/envInspection/referOrUnrefer";

    public static final String carInspection_query = "/carInspection/query";
    public static final String carInspection_saveOrUpdateDoc = "/carInspection/saveOrUpdateDoc";
    public static final String carInspection_deleteDoc = "/carInspection/deleteDoc";
    public static final String carInspection_findOne = "/carInspection/findOne";
    public static final String carInspection_referOrUnrefer = "/carInspection/referOrUnrefer";

    public static final String goodsInspection_query = "/goodsInspection/query";
    public static final String goodsInspection_saveOrUpdateDoc = "/goodsInspection/saveOrUpdateDoc";
    public static final String goodsInspection_deleteDoc = "/goodsInspection/deleteDoc";
    public static final String goodsInspection_findOne = "/goodsInspection/findOne";
    public static final String goodsInspection_referOrUnrefer = "/goodsInspection/referOrUnrefer";

    public static final String pigInspection_query = "/pigInspection/query";
    public static final String pigInspection_saveOrUpdateDoc = "/pigInspection/saveOrUpdateDoc";
    public static final String pigInspection_deleteDoc = "/pigInspection/deleteDoc";
    public static final String pigInspection_findOne = "/pigInspection/findOne";
    public static final String pigInspection_referOrUnrefer = "/pigInspection/referOrUnrefer";

    public static final String isolationRegist_query = "/isolationRegist/query";
    public static final String isolationRegist_saveOrUpdateDoc = "/isolationRegist/saveOrUpdateDoc";
    public static final String isolationRegist_deleteDoc = "/isolationRegist/deleteDoc";
    public static final String isolationRegist_findOne = "/isolationRegist/findOne";
    public static final String isolationRegist_referOrUnrefer = "/isolationRegist/referOrUnrefer";

    public static final String isolationRegist_tification_query = "/isolationRegist/tification/query";
    public static final String isolationRegist_tification_deleteDoc = "/isolationRegist/tification/deleteDoc";
    public static final String isolationRegist_tification_updateDoc = "/isolationRegist/tification/updateDoc";
    public static final String isolationRegist_tification_findOne = "/isolationRegist/tification/findOne";
    public static final String isolationRegist_tification_referOrUnrefer = "/isolationRegist/tification/referOrUnrefer";

    public static final String enterFactory_query = "/enterFactory/query";
    public static final String enterFactory_saveOrUpdateDoc = "/enterFactory/saveOrUpdateDoc";
    public static final String enterFactory_deleteDoc = "/enterFactory/deleteDoc";
    public static final String enterFactory_findOne = "/enterFactory/findOne";
    public static final String enterFactory_referOrUnrefer = "/enterFactory/referOrUnrefer";



//##################################################生物安全#############################################################


}
