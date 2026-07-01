package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    // 允许无需登录即可访问的路径
    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList(
            "/login.jsp",
            "/login",
            "/logout"
    ));

    // 只读用户允许访问的路径前缀（仅限 GET 请求）
    private static final Set<String> READONLY_ALLOWED_PREFIXES = new HashSet<>(Arrays.asList(
            "/inventory",
            "/inventoryLog",
            "/salesStatistics",
            "/windowFunctions",
            "/product",
            "/category",
            "/supplier",
            "/customer"
    ));

    // 业务员和管理员都禁止访问的路径（仅管理员可访问）
    private static final Set<String> ADMIN_ONLY_PREFIXES = new HashSet<>(Arrays.asList(
            "/user",
            "/backup"
    ));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // 1. 放行静态资源
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 放行白名单（登录页、登录请求）
        if (WHITE_LIST.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. 检查是否已登录
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        int roleId = currentUser.getRoleId();

        // 4. 管理员：全部放行
        if (roleId == 1) {
            chain.doFilter(request, response);
            return;
        }

        // 5. 业务员（roleId=2）：禁止访问管理员专属路径
        if (roleId == 2) {
            for (String prefix : ADMIN_ONLY_PREFIXES) {
                if (path.startsWith(prefix)) {
                    resp.sendRedirect(contextPath + "/403.jsp");
                    return;
                }
            }
            chain.doFilter(request, response);
            return;
        }

        // 6. 只读用户（roleId=3）：严格限制
        if (roleId == 3) {
            // 6.1 禁止所有 POST 请求
            if ("POST".equalsIgnoreCase(req.getMethod())) {
                resp.sendRedirect(contextPath + "/403.jsp");
                return;
            }

            // 6.2 检查路径是否在允许列表中
            boolean allowed = false;
            for (String prefix : READONLY_ALLOWED_PREFIXES) {
                if (path.startsWith(prefix)) {
                    allowed = true;
                    break;
                }
            }

            // 6.3 额外放行：直接访问 JSP 页面（但后续 JSP 内部会隐藏按钮）
            if (path.endsWith(".jsp")) {
                allowed = true;
            }

            if (!allowed) {
                resp.sendRedirect(contextPath + "/403.jsp");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        // 默认拒绝
        resp.sendRedirect(contextPath + "/403.jsp");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}