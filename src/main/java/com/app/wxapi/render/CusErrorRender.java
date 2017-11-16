package com.app.wxapi.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;

import java.io.IOException;
import java.io.PrintWriter;

public class CusErrorRender extends Render {
    protected static final String contentType = "text/html; charset=" + getEncoding();

    protected static final String version = "<center><a href='http://www.abhwllm.com?f=ev-1.0' target='_blank'><b>Powered by abhwllm 1.0</b></a></center>";

    protected static final String html404 = "<html><head><title>404 Not Found</title></head><body bgcolor='white'><center><h1>404 Not Found</h1></center><hr>" + version + "</body></html>";
    protected static final String html500 = "<html><head><title>500 Internal Server Error</title></head><body bgcolor='white'><center><h1>用户信息错误，请重新关注！</h1></center><hr>" + version + "</body></html>";

    protected static final String html401 = "<html><head><title>401 Unauthorized</title></head><body bgcolor='white'><center><h1>401 Unauthorized</h1></center><hr>" + version + "</body></html>";
    protected static final String html403 = "<html><head><title>403 Forbidden</title></head><body bgcolor='white'><center><h1>403 Forbidden</h1></center><hr>" + version + "</body></html>";

    protected int errorCode;

    public CusErrorRender(int errorCode, String view) {
        this.errorCode = errorCode;
        this.view = view;
    }

    public void render() {
        response.setStatus(getErrorCode());    // HttpServletResponse.SC_XXX_XXX

        // render with view
        String view = getView();
        if (view != null) {
            RenderManager.me().getRenderFactory().getRender(view).setContext(request, response).render();
            return;
        }

        // render with html content
        PrintWriter writer = null;
        try {
            response.setContentType(contentType);
            writer = response.getWriter();
            writer.write(getErrorHtml());
            writer.flush();
        } catch (IOException e) {
            throw new RenderException(e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public String getErrorHtml() {
        int errorCode = getErrorCode();
        if (errorCode == 404)
            return html404;
        if (errorCode == 500)
            return html500;
        if (errorCode == 401)
            return html401;
        if (errorCode == 403)
            return html403;
        return "<html><head><title>" + errorCode + " Error</title></head><body bgcolor='white'><center><h1>" + errorCode + " Error</h1></center><hr>" + version + "</body></html>";
    }

    public int getErrorCode() {
        return errorCode;
    }
}
