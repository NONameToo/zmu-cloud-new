package com.zmu.cloud.sysadmin.organization.service.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.sysadmin.organization.dao.GroupMapper;
import com.zmu.cloud.sysadmin.organization.dao.UserGroupMapper;
import com.zmu.cloud.sysadmin.organization.entity.param.GroupQueryParam;
import com.zmu.cloud.sysadmin.organization.entity.po.Group;
import com.zmu.cloud.sysadmin.organization.entity.po.User;
import com.zmu.cloud.sysadmin.organization.entity.po.UserGroup;
import com.zmu.cloud.sysadmin.organization.service.IGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GroupService extends ServiceImpl<GroupMapper, Group> implements IGroupService {

    @Autowired
    UserService userService;

    @Autowired
    UserGroupMapper userGroupMapper;

    @Autowired
    GroupMapper groupMapper;

    @Override
    public boolean add(Group group) {
        return this.save(group);
    }

    @Override
    @CacheInvalidate(name = "group::", key = "#id")
    public boolean delete(String id) {
        return this.removeById(id);
    }

    @Override
    @CacheInvalidate(name = "group::", key = "#group.id")
    public boolean update(Group group) {
        return this.updateById(group);
    }

    @Override
    @Cached(name = "group::", key = "#id", cacheType = CacheType.BOTH)
    public Group get(String id) {
        return this.getById(id);
    }

    @Override
    public List<Group> query(GroupQueryParam groupQueryParam) {
        QueryWrapper<Group> queryWrapper = groupQueryParam.build();
        queryWrapper.eq("name", groupQueryParam.getName());
        return this.list(queryWrapper);
    }

    @Override
    public List<Group> queryByParentId(String id) {
        return this.list(new QueryWrapper<Group>().eq("parent_id", id));
    }

    @Override
    public List<Group> queryByUserName(String userName) {
        //get user
        User user = userService.getByUniqueId(userName);
        LambdaQueryWrapper<UserGroup> userGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userGroupLambdaQueryWrapper.eq(UserGroup::getUserId,user.getId());

        //get groupIds
        List<UserGroup> userGroupList = userGroupMapper.selectList(userGroupLambdaQueryWrapper);
        Set<String> groupIds = userGroupList.stream().map(UserGroup::getGroupId).collect(Collectors.toSet());

        //get groups
        LambdaQueryWrapper<Group> groupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        groupLambdaQueryWrapper.in(Group::getId,groupIds);
        return groupMapper.selectList(groupLambdaQueryWrapper);
    }
}
