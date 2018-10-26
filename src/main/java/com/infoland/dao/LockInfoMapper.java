package com.infoland.dao;

import com.infoland.model.LockInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface LockInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LockInfo record);

    int insertSelective(LockInfo record);

    LockInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LockInfo record);

    int updateByPrimaryKey(LockInfo record);
}