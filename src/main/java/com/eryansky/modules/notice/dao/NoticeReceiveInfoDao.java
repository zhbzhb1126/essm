/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;

import java.util.List;
import java.util.Map;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-15 
 */
@MyBatisDao
public interface NoticeReceiveInfoDao extends CrudDao<NoticeReceiveInfo> {
    List<NoticeReceiveInfo> findQueryList(Map<String,Object> parameter);

    NoticeReceiveInfo getUserNotice(NoticeReceiveInfo noticeReceiveInfo);

    List<NoticeReceiveInfo> findUserUnreadNotices(NoticeReceiveInfo noticeReceiveInfo);
}
