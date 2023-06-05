//package com.ihankun.core.db.dynamic;
//
//import com.alibaba.druid.util.JdbcUtils;
//import com.ihankun.core.db.dynamic.bean.KunDataSourceInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.jasypt.util.text.AES256TextEncryptor;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.validation.constraints.NotEmpty;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class DataSourceInfoHolder {
//
//    public static final String PUBLIC_KEY = "KUN_DS_PUBLIC_KEY";
//    private static final String DS_PREFIX = "ihankun.ds.db.";
//    private static final String DS_POINT = ".";
//
//    @Resource
//    Environment environment;
//
//    private static final String URL_PREFIX = "jdbc:postgresql://";
//
//    private static final String URL_SUFFIX = "?useUnicode=true&characterEncoding=utf8&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai";
//
//    /**
//     * 设置environment，仅测试用
//     *
//     * @param environment environment
//     */
//    public void mockEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//
//    /**
//     * 根据域名、数据库名称(HIS_MASTER等)、用户名，获取匹配的数据库ip端口密码信息
//     * 该接口仅从nacos配置中获取信息
//     * 受到老环境密码修改的影响
//     * 在新环境可正常使用，在老环境下可能返回错误的密码
//     *
//     * @param domain   域名
//     * @param db       数据库名称(HIS_MASTER等)
//     * @param userName 用户名
//     * @return com.ihankun.core.db.dynamic.bean.KunDataSourceInfo
//     */
//    public KunDataSourceInfo getDataSourceInfo(@NotEmpty String domain, @NotEmpty String db, @NotEmpty String userName) {
//        return getDataSourceInfo(domain, db, userName, null, false);
//    }
//
//    /**
//     * 根据域名、数据库名称(HIS_MASTER等)、用户名，获取匹配的数据库ip端口密码信息
//     * 该接口仅从nacos配置中获取信息
//     * 在新环境可正常使用
//     * 如果checkPassword为false，在老环境下可能返回错误的密码
//     * 如果为true，会在返回前检查密码正常性，返回正常的密码，但是效率低于不检测（需要连接数据库检测密码是否正确），
//     *
//     * @param domain        域名
//     * @param db            数据库名称(HIS_MASTER等)
//     * @param userName      用户名
//     * @param pgDb          pg中的数据库名称（chis，chisapp），如果不检查密码正常性可传null
//     * @param checkPassword 是否确认密码正常性
//     * @return com.ihankun.core.db.dynamic.bean.KunDataSourceInfo
//     */
//    public KunDataSourceInfo getDataSourceInfo(@NotEmpty String domain, @NotEmpty String db, @NotEmpty String userName, String pgDb, Boolean checkPassword) {
//        final String pre = DS_PREFIX + domain + DS_POINT;
//
//        String ipPort = environment.getProperty(pre + db);
//        if (StringUtils.isBlank(ipPort)) {
//            log.error("DataSourceInfoHolder.getDataSourceInfo.ipPort.get.fail,domain={},db={},user={}", domain, db, userName);
//            return null;
//        }
//
//        String password = environment.getProperty(pre + userName + DS_POINT + db);
//        if (StringUtils.isBlank(password)) {
//            log.warn("DataSourceInfoHolder.getDataSourceInfo.get.password.from.database.null,domain={},db={},user={}", domain, db, userName);
//
//            password = environment.getProperty(pre + userName);
//            if (StringUtils.isBlank(password)) {
//                log.error("DataSourceInfoHolder.getDataSourceInfo.get.password.null,domain={},db={},user={}", domain, db, userName);
//            }
//        }
//        String key = environment.getProperty(PUBLIC_KEY);
//        password = decryptPass(key, password);
//        log.info("DataSourceInfoHolder.getDataSourceInfo.get.password,domain={},db={},user={}", domain, db, userName);
//        if (checkPassword) {
//            if (!checkPassword(ipPort, pgDb, userName, password)) {
//                log.info("DataSourceInfoHolder.getDataSourceInfo.check.password.failed,domain={},db={},user={}", domain, db, userName);
//                password = null;
//            }
//        }
//        if (StringUtils.isBlank(password)) {
//            String dbKey = environment.getProperty(DataSourceCacheCreator.DB_BUILD_PRE + domain);
//            String useEnv = environment.getProperty(DataSourceCacheCreator.USE_ENV_KEY);
//            if (!StringUtils.isBlank(dbKey)) {
//                if (key == null) {
//                    key = "";
//                }
//                if (Boolean.TRUE.toString().equals(useEnv)) {
//                    key = "";
//                }
//                if (StringUtils.isBlank(key)) {
//                    log.warn("DataSourceInfoHolder.getDataSourceInfo.get.password.env.key.null,domain={},db={},user={}", domain, db, userName);
//                }
//                password = DataSourceCacheCreator.buildPass(key, dbKey, userName);
//                if (checkPassword && (!checkPassword(ipPort, pgDb, userName, password))) {
//                    log.info("DataSourceInfoHolder.getDataSourceInfo.check.password.failed,domain={},db={},user={}", domain, db, userName);
//                    password = null;
//                }
//            } else {
//                log.error("DataSourceInfoHolder.getDataSourceInfo.get.password.db.key.null,domain={},db={},user={}", domain, db, userName);
//            }
//        }
//        String[] ipPortSplit = ipPort.split(":");
//        return KunDataSourceInfo.builder().
//                ip(ipPortSplit[0]).
//                port(ipPortSplit[1]).
//                password(password).
//                build();
//    }
//
//    private static boolean checkPassword(String ipPort, String db, String userName, String pass) {
//        String url = URL_PREFIX + ipPort + "/" + db + URL_SUFFIX;
//        Connection connection = null;
//        try {
//            connection = DriverManager.getConnection(url, userName, pass);
//            return true;
//        } catch (SQLException e) {
//            return false;
//        } finally {
//            JdbcUtils.close(connection);
//        }
//    }
//
//    private static String decryptPass(String key, String pass) {
//        if (StringUtils.isNotBlank(key)) {
//            // 解密密码
//            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
//            textEncryptor.setPassword(key);
//            try {
//                pass = textEncryptor.decrypt(pass);
//                log.info("DataSourceInfoHolder.getDataSourceInfo.pass.decrypt.success!");
//            } catch (Exception e) {
//                log.info("DataSourceInfoHolder.getDataSourceInfo.pass.decrypt.fail!");
//            }
//        } else {
//            log.info("DataSourceInfoHolder.getDataSourceInfo.encryptor.key.not.find!");
//        }
//        return pass;
//    }
//}
