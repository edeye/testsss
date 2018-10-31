package com.infoland.dao;

import com.infoland.model.LockUser;
import org.springframework.stereotype.Repository;

@Repository
public interface LockUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LockUser record);

    int insertSelective(LockUser record);

    LockUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LockUser record);

    int updateByPrimaryKey(LockUser record);

    int updateDbool(LockUser lockUser);

    LockUser selectByCardIdAndPassword(LockUser lockUser);
}