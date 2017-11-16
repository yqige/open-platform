package com.app.wxapi.directive;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;

import java.io.Writer;
import java.math.BigDecimal;

public class NumDirective extends Directive {

    private Expr valueExpr;
    private Expr numPatternExpr;
    private int paraNum;

    public void setExprList(ExprList exprList) {
        this.paraNum = exprList.length();
        if (paraNum > 2) {
            throw new ParseException("Wrong number parameter of #date directive, two parameters allowed at most", location);
        }

        if (paraNum == 0) {
            this.valueExpr = null;
            this.numPatternExpr = null;
        } else if (paraNum == 1) {
            this.valueExpr = exprList.getExprArray()[0];
            this.numPatternExpr = null;
        } else if (paraNum == 2) {
            this.valueExpr = exprList.getExprArray()[0];
            this.numPatternExpr = exprList.getExprArray()[1];
        }
    }

    public void exec(Env env, Scope scope, Writer writer) {
        if (paraNum == 0) {
            output(env, writer);
        } else if (paraNum == 1) {
            outputWithoutNumPattern(env, scope, writer);
        } else if (paraNum == 2) {
            outputWithNumPattern(env, scope, writer);
        }
    }

    private void output(Env env, Writer writer) {
        Object value = format(new java.util.Date(), env.getEngineConfig().getDatePattern());
        write(writer, value.toString());
    }

    private void outputWithoutNumPattern(Env env, Scope scope, Writer writer) {
        Object value = valueExpr.eval(scope);
        if (value != null) {
            value = format(value, env.getEngineConfig().getDatePattern());
            write(writer, value.toString());
        }
    }

    private void outputWithNumPattern(Env env, Scope scope, Writer writer) {
        Object value = valueExpr.eval(scope);
        if (value == null) {
            return;
        }

        Object dp = this.numPatternExpr.eval(scope).toString();
        if (!(dp instanceof String)) {
            throw new TemplateException("The sencond parameter dataPattern of #date directive must be String", location);
        }
        value = format(value, (String) dp);
        write(writer, value.toString());
    }

    private String format(Object value, String numPattern) {
        try {
            if(numPattern.equals("2"))
                return new BigDecimal(value.toString()).setScale(2, 4).negate().toString();
            else if(numPattern.equals("0"))
                return new BigDecimal(value.toString()).setScale(2, 4).negate().toString();
            else
                return new BigDecimal(value.toString()).setScale(2, 4).toString();
        } catch (Exception e) {
            throw new TemplateException(e.getMessage(), location, e);
        }
    }
}
