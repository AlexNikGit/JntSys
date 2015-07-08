package test.view;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Map;


/**
 * Created by alex_nik on 06.07.15.
 */
public abstract class XProcView extends AbstractUrlBasedView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        OutputStream  = genResult(request.getRequestURI());

    }

    protected void genResult(String request HttpServletResponse) throws Exception {

        OutputStream PipelineData = new OutputStream();
        return PipelineData;
    }
}
