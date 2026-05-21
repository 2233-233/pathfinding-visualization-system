package com.ldk.backend.service;

import com.ldk.backend.commons.R;

import java.util.Map;

public interface AdminDashboardService {
    /**
     * 获取管理员仪表板统计数据
     * @return 包含所有仪表板数据的Map
     */
    R<Map<String, Object>> getDashboardData();
}
