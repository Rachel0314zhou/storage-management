package servlet;

import db.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 数据库备份与恢复控制器（Servlet）
 * 主要功能：执行数据库备份（mysqldump）和恢复（mysql）
 * 数据来源：通过调用操作系统外部命令实现，配置统一从 DB.java 获取
 * 访问地址：/backup
 *
 * 权限要求：仅管理员（roleId=1）可访问
 */
@WebServlet("/backup")
public class BackupServlet extends HttpServlet {

    // 从 DB.java 统一获取配置（路径、用户名、密码、数据库名）
    private static final String MYSQL_PATH = DB.getMysqlBinPath();
    private static final String DB_NAME = DB.getDbName();
    private static final String USER = DB.getUsername();
    private static final String PASSWORD = DB.getPassword();

    /**
     * 处理 GET 请求：根据 action 参数分发到备份或恢复操作
     * 权限校验：仅管理员（roleId=1）可执行
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // ===== 权限校验：仅管理员（roleId=1）可以访问备份恢复功能 =====
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRoleId() != 1) {
            resp.sendRedirect(req.getContextPath() + "/403.jsp");
            return;
        }

        String action = req.getParameter("action");

        String msg = "";
        try {
            if ("backup".equals(action)) {
                String userSaveDir = req.getParameter("saveDir");
                msg = doBackup(userSaveDir);
            } else if ("restore".equals(action)) {
                String filePath = req.getParameter("filePath");
                msg = doRestore(filePath);
            } else {
                msg = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "系统异常：" + e.getMessage();
        }

        req.setAttribute("msg", msg);
        req.getRequestDispatcher("/backup.jsp").forward(req, resp);
    }

    /**
     * 执行数据库备份：调用 mysqldump 命令导出 .sql 文件
     * 支持用户自定义保存路径，路径为空时使用项目根目录
     */
    private String doBackup(String userSaveDir) {
        try {
            // 1. 确定保存目录（用户自定义 或 项目根目录）
            String saveDir;
            if (userSaveDir == null || userSaveDir.trim().isEmpty()) {
                saveDir = getServletContext().getRealPath("/");
            } else {
                saveDir = userSaveDir.trim();
                if (!saveDir.endsWith(File.separator) && !saveDir.endsWith("/")) {
                    saveDir += File.separator;
                }
                File dir = new File(saveDir);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    if (!created) {
                        return "备份失败：无法创建目录 " + saveDir + "，请检查权限或路径是否正确。";
                    }
                }
                if (!dir.canWrite()) {
                    return "备份失败：目录 " + saveDir + " 没有写入权限，请更换路径。";
                }
            }

            // 2. 生成备份文件名（带时间戳）
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "backup_" + timestamp + ".sql";
            String backupFile = saveDir + fileName;

            // 3. 构建并执行 mysqldump 命令
            String cmd = MYSQL_PATH + "mysqldump -u" + USER + " -p" + PASSWORD + " " + DB_NAME + " -r " + backupFile;
            System.out.println("执行命令：" + cmd);

            Process process = Runtime.getRuntime().exec(cmd);

            // 4. 读取错误流（用于诊断失败原因）
            StringBuilder errorMsg = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorMsg.append(line).append("\n");
                }
            }

            // 5. 等待命令执行完成并判断结果
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "备份成功！\n文件名：" + fileName + "\n保存位置：" + saveDir;
            } else {
                String errorDetail = errorMsg.toString().trim();
                if (errorDetail.isEmpty()) {
                    errorDetail = "未知错误（请检查MySQL路径/密码/数据库名是否正确）";
                }
                return "备份失败（退出码 " + exitCode + "）\n详情：" + errorDetail;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "备份异常：" + e.getMessage();
        }
    }

    /**
     * 执行数据库恢复：调用 mysql 命令导入 .sql 文件
     * 需要用户提供文件的完整路径
     */
    private String doRestore(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return "请输入备份文件的完整路径";
        }

        try {
            // 1. 规范化文件路径
            String fullPath = filePath.trim().replace("\\", "/");
            File file = new File(fullPath);

            // 2. 校验文件是否存在
            if (!file.exists()) {
                return "文件不存在：" + fullPath + "\n请检查路径是否正确（注意：路径末尾不要有多余空格）";
            }

            // 3. 校验是否为文件（而非目录）
            if (file.isDirectory()) {
                return "路径指向的是目录，请指定具体的 .sql 文件";
            }

            // 4. 校验文件是否可读
            if (!file.canRead()) {
                return "文件无法读取，请检查文件权限";
            }

            // 5. 构建并执行 mysql 恢复命令
            String cmd = MYSQL_PATH + "mysql -u" + USER + " -p" + PASSWORD + " " + DB_NAME + " -e \"source " + fullPath + "\"";
            System.out.println("执行命令：" + cmd);

            Process process = Runtime.getRuntime().exec(cmd);

            // 6. 读取错误流
            StringBuilder errorMsg = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorMsg.append(line).append("\n");
                }
            }

            // 7. 等待命令执行完成并判断结果
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "恢复成功！\n数据已从 " + file.getName() + " 恢复。\n来源：" + file.getParent();
            } else {
                String errorDetail = errorMsg.toString().trim();
                if (errorDetail.isEmpty()) {
                    errorDetail = "未知错误（请检查备份文件是否完整）";
                }
                return "恢复失败（退出码 " + exitCode + "）\n详情：" + errorDetail;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "恢复异常：" + e.getMessage();
        }
    }
}