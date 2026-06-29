package com.factor.interfaces.rest;

import com.factor.common.api.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 仪表盘控制器测试
 */
class DashboardControllerTest {

    private DashboardController controller;

    @BeforeEach
    void setUp() {
        controller = new DashboardController();
    }

    @Test
    void testDashboardSystemAdmin() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "SYSTEM_ADMIN");

        assertTrue(response.success());
        assertEquals("OK", response.message());
        assertNotNull(response.data());

        Map<String, Object> data = response.data();
        assertEquals("平台治理总览", data.get("title"));
        assertEquals("系统超级管理员后台", data.get("kicker"));
        assertNotNull(data.get("nav"));
        assertNotNull(data.get("stats"));
        assertNotNull(data.get("tasks"));
    }

    @Test
    void testDashboardTrader() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "TRADER");

        assertTrue(response.success());
        assertNotNull(response.data());

        Map<String, Object> data = response.data();
        assertEquals("交易执行看板", data.get("title"));
        assertEquals("交易员工作台", data.get("kicker"));
        assertNotNull(data.get("nav"));
        assertNotNull(data.get("stats"));
    }

    @Test
    void testDashboardCustomer() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "CUSTOMER");

        assertTrue(response.success());
        assertNotNull(response.data());

        Map<String, Object> data = response.data();
        assertEquals("我的资产首页", data.get("title"));
        assertEquals("客户门户", data.get("kicker"));
        assertNotNull(data.get("nav"));
        assertNotNull(data.get("stats"));
    }

    @Test
    void testDashboardWithHeader() {
        ApiResponse<Map<String, Object>> response = controller.dashboard("TRADER", null);

        assertTrue(response.success());
        assertNotNull(response.data());

        Map<String, Object> data = response.data();
        assertEquals("交易执行看板", data.get("title"));
    }

    @Test
    void testDashboardDefaultRole() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, null);

        assertTrue(response.success());
        assertNotNull(response.data());

        Map<String, Object> data = response.data();
        assertEquals("平台治理总览", data.get("title"));
    }

    @Test
    void testDashboardSystemAdminStats() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "SYSTEM_ADMIN");

        Map<String, Object> data = response.data();
        assertNotNull(data.get("stats"));

        java.util.List<Map<String, Object>> stats = (java.util.List<Map<String, Object>>) data.get("stats");
        assertTrue(stats.size() > 0);
        assertEquals("租户数量", stats.get(0).get("label"));
        assertEquals("机构数量", stats.get(1).get("label"));
        assertEquals("账号数量", stats.get(2).get("label"));
        assertEquals("角色数量", stats.get(3).get("label"));
    }

    @Test
    void testDashboardTraderStats() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "TRADER");

        Map<String, Object> data = response.data();
        assertNotNull(data.get("stats"));

        java.util.List<Map<String, Object>> stats = (java.util.List<Map<String, Object>>) data.get("stats");
        assertTrue(stats.size() > 0);
        assertEquals("银子账户总数", stats.get(0).get("label"));
        assertEquals("可用资金", stats.get(1).get("label"));
        assertEquals("冻结金额", stats.get(2).get("label"));
        assertEquals("待处理交易", stats.get(3).get("label"));
    }

    @Test
    void testDashboardCustomerStats() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "CUSTOMER");

        Map<String, Object> data = response.data();
        assertNotNull(data.get("stats"));

        java.util.List<Map<String, Object>> stats = (java.util.List<Map<String, Object>>) data.get("stats");
        assertTrue(stats.size() > 0);
        assertEquals("我的总资产", stats.get(0).get("label"));
        assertEquals("今日收益", stats.get(1).get("label"));
        assertEquals("累计收益", stats.get(2).get("label"));
        assertEquals("当前组合", stats.get(3).get("label"));
    }

    @Test
    void testDashboardSystemAdminTable() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "SYSTEM_ADMIN");

        Map<String, Object> data = response.data();
        assertEquals("最近系统操作", data.get("tableTitle"));
        assertNotNull(data.get("table"));
    }

    @Test
    void testDashboardTraderTable() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "TRADER");

        Map<String, Object> data = response.data();
        assertEquals("最新交易流水", data.get("tableTitle"));
        assertNotNull(data.get("table"));
    }

    @Test
    void testDashboardCustomerTable() {
        ApiResponse<Map<String, Object>> response = controller.dashboard(null, "CUSTOMER");

        Map<String, Object> data = response.data();
        assertEquals("近期组合动态", data.get("tableTitle"));
        assertNotNull(data.get("table"));
    }

    @Test
    void testDashboardRolePriority() {
        // 测试role参数优先于header
        ApiResponse<Map<String, Object>> response = controller.dashboard("CUSTOMER", "TRADER");

        Map<String, Object> data = response.data();
        assertEquals("交易执行看板", data.get("title"));
    }
}