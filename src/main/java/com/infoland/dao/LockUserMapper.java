package com.infoland.dao;

import com.infoland.model.LockUser;

public interface LockUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LockUser record);

    int insertSelective(LockUser record);

    LockUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LockUser record);

    int updateByPrimaryKey(LockUser record);
}