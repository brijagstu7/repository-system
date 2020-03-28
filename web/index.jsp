<%@ page import="java.util.Date" %>
<%@ page import="services.UserServices" %>
<%@ page import="java.util.List" %>
<%@ page import="servlets.UserServlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%--
  Created by IntelliJ IDEA.
  User: yang_sijie
  Date: 2019-06-21
  Time: 14:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">


<%--消除滚动条：--%>
<html style="overflow:hidden;">
  <head>
    <title>$Title$</title>
    <link href="dist/css/zui.min.css" rel="stylesheet">

    <style>
      *{
        padding-left: 90px;
        padding-right: 90px;
      }

      .panel{
        float: right;
      }

      .form-control{
        width: max-content;
        margin-left: 0;
      }

      form{
        padding: 0;
      }
      
      label{
        text-align: left;
        padding: 0;
      }
      
      .form-group{
        padding: 0;
      }



    </style>
  </head>
  <body>
<%--  <%  String[] tables = UserServices.showTables().toArray(new String[0]); %>--%>

    <script src="http://code.jquery.com/jquery-latest.js"></script>
    <script src="dist/js/zui.min.js"></script>
    <script>


      <%--var x = '${requestStatus }';--%>

      <% if (UserServlet.regName.equals("none") && UserServlet.requestStatus == UserServlet.SUCCESS && UserServlet.tblcontent==null){
          UserServlet.currentState = 1;
      }else if (UserServlet.regName.equals("none") && UserServlet.requestStatus == UserServlet.WRONG_NAME && UserServlet.tblcontent==null){
          UserServlet.currentState = 2;
      }else if (UserServlet.regName.equals("none") && UserServlet.requestStatus == UserServlet.WRONG_PASSWORD && UserServlet.tblcontent==null){
          UserServlet.currentState = 3;
      }else if (!UserServlet.regName.equals("none") && UserServlet.requestStatus == UserServlet.SUCCESS && UserServlet.tblcontent==null){
          UserServlet.currentState = 4;
      }else if (!UserServlet.regName.equals("none") && UserServlet.requestStatus == UserServlet.OTHER && UserServlet.tblcontent!=null){
          UserServlet.currentState = 5;
      }else {
          //       abnormal state     ?
      }

      %>


      // alert(x);
      window.onload = function(){

        document.getElementById("username").innerText = "<%=UserServlet.regName%>";

        <%--console.log(${requestStatus });--%>

        switch (<%=UserServlet.requestStatus%>) {
          case <%=UserServlet.SUCCESS%>: break;
          case <%=UserServlet.WRONG_NAME%>:
              alert("wrong name");
              break;
          case <%=UserServlet.WRONG_PASSWORD%>:
              alert("wrong password");
              break;
          case <%=UserServlet.OTHER%>: break;
        }


        <%--if (x) {--%>
        <%--  switch (x) {--%>
        <%--    case '<%=UserServlet.SUCCESS%>':--%>
        <%--      break;--%>
        <%--    case '<%=UserServlet.WRONG_NAME%>':--%>
        <%--      $('.btn').on('click', function () {--%>
        <%--        new $.zui.Messager('WRONG NAME.', {--%>
        <%--          icon: 'bell', // 定义消息图标--%>
        <%--          placement: 'center'--%>
        <%--        }).show();--%>
        <%--      });--%>
        <%--      break;--%>
        <%--    case '<%=UserServlet.WRONG_PASSWORD%>':--%>
        <%--      $('.btn').on('click', function () {--%>
        <%--        new $.zui.Messager('WRONG PASSWORD.', {--%>
        <%--          icon: 'bell', // 定义消息图标--%>
        <%--          placement: 'center'--%>
        <%--        }).show();--%>
        <%--      });--%>
        <%--      break;--%>
        <%--    case '<%=UserServlet.OTHER%>':--%>
        <%--      break;--%>
        <%--  }--%>
        <%--}--%>

        <%--document.getElementById("table-content").innerText = '${tables}';--%>
        <%--var tables = '<%=UserServlet.tblcontent%>';--%>
        <%--console.log(tables);--%>

          var form_to_show = '${form_to_show}';
          if (form_to_show !== ''){
              if (form_to_show === '!not_formatted'){

                  alert("输入格式不规范");

              }else {

                  document.getElementById("req-alt").hidden=true;
                  document.getElementById("alt-form").hidden =false;

                  document.getElementById(form_to_show).hidden = false;
              }
          }

          var update_reply = '${update_reply}';
          if(update_reply === 'insert'){
              alert("系统没有能够对数据库进行任何修改。出现这种情况可能是数据库的插入异常，也可能是因为你没有权限访问与这个数据相关的表。");
          }else if (update_reply === 'delete'){
              alert("系统没有能够对数据库进行任何修改。出现这种情况可能是数据库的删除异常，也可能是因为你没有权限访问与这个数据相关的表。");
          }

          var table_changed = '${table_changed}';
          if (table_changed){
              document.getElementById("table-content").innerText=document.getElementById("table-content").innerText.concat("(这是操作成功后的相关货架表)");
          }

          var faults = '${faults}';
          if (faults){
              alert(faults);
          }

        if ("<%=UserServlet.regName%>" === "none") {
          document.getElementById("login").hidden = false;
        } else {
          document.getElementById("mni-form").hidden = false;
          document.getElementById("reg-form").innerHTML = "if you want to register, please log out.";

        }

        window.onresize = function () {
          //the position of panel-bkg follows the window's innerWidth
          // document.getElementById("panel-bkg").style.width = window.innerWidth+"px";
        };

        <%List<String> list =  UserServices.showTables();%>

        //jsp returns string value without "", which cannot be detected as strings in javascript.
        <%
        for (int i=0;i<list.size();i++) {
            String s = list.get(i);
            StringBuilder sb = new StringBuilder(s);
            sb.insert(0,"\"");
            sb.append("\"");
            s = sb.toString();
            list.set(i,s);
        }
        %>

        //List in java can be comprehended as arrays in javascript
        var tables = <%= list%>;
        
        if (document.getElementById("reg-tables")) 
        document.getElementById("reg-tables").innerHTML = (function () {
          var html = "";
          for (var i = 0; i < tables.length; i++) {

            if (tables[i] === "users") continue;

            html += "<label class=\"checkbox-inline\" for=\"reg-checkbox-table" + i + "\">\n" +
                    "        <input type=\"checkbox\"  name=\"reg-checkbox-table" + i + "\" id=\"reg-checkbox-table" + i + "\"  value=\""+tables[i]+"\"> " + tables[i] + "\n" +
                    "      </label>\n";
          }
          return html;
        })();

          if (document.getElementById("mod-tables"))
              document.getElementById("mod-tables").innerHTML=(function () {
            var html = "";
            for (var i = 0; i < tables.length; i++) {

              if (tables[i] === "users") continue;

              html += "<label class=\"checkbox-inline\" for=\"mod-checkbox-table" + i + "\">\n" +
                      "        <input type=\"checkbox\"  name=\"mod-checkbox-table" + i + "\" id=\"mod-checkbox-table" + i + "\"  value=\""+tables[i]+"\"> " + tables[i] + "\n" +
                      "      </label>\n";
            }
            return html;
          })();

          if (document.getElementById("tbl-req"))
            document.getElementById("tbl-req").innerHTML=(function () {
              var html = "";
              for (var i = 0; i < tables.length; i++) {

                if (tables[i] === "users") continue;

                html += "<label class=\"checkbox-inline\" for=\"req-checkbox-table" + i + "\">\n" +
                        "        <input type=\"checkbox\"  name=\"req-checkbox-table" + i + "\" id=\"req-checkbox-table" + i + "\"  value=\""+tables[i]+"\"> " + tables[i] + "\n" +
                        "      </label>\n";
              }

              html+="<label class=\"checkbox-inline\" for=\"req-checkbox-table\">\n" +
                      "        <input type=\"checkbox\"  name=\"req-checkbox-table\" id=\"req-checkbox-table\" hidden=true checked  value=\"0\"> \n" +
                      "      </label>\n";


          return html;

        })();

        


        var inputs = Array.from(document.getElementsByTagName("input"));

        inputs.forEach(function (cur) {
          cur.name = cur.id;
        });

        window.onresize = function () {
          //the position of panel-bkg follows the window's innerWidth
          // document.getElementById("panel-bkg").style.width = window.innerWidth+"px";
        }





      };



      function mod() {

        // document.getElementById("tbl-form").hidden =true;
        document.getElementById("mod-form").hidden =false;
        document.getElementById("reg-form").hidden =true;
        document.getElementById("login").hidden =true;
        document.getElementById("mni-form").hidden =true;
          document.getElementById("alt-form").hidden =true;


      }

      function reg() {


        // document.getElementById("tbl-form").hidden =true;
        document.getElementById("mod-form").hidden =true;
        document.getElementById("login").hidden =true;
        document.getElementById("reg-form").hidden =false;
        document.getElementById("mni-form").hidden =true;
          document.getElementById("alt-form").hidden =true;



      }

      function tbl() {

        document.getElementById("mod-form").hidden =true;
        document.getElementById("reg-form").hidden =true;


        if (document.getElementById("username").innerText==="none"){
            document.getElementById("login").hidden = false;
        }else {
            document.getElementById("mni-form").hidden = false;
        }


      }

      function change_user() {
<%--        <%UserServlet.regName="none";--%>
<%--            UserServlet.requestStatus = UserServlet.SUCCESS;--%>
<%--            UserServlet.tblcontent = null;--%>
<%--        %>--%>
<%--        document.getElementById("username").innerText = "none";--%>

          document.getElementById("logout").value = "0";
      }


      <% UserServlet.lastState = UserServlet.currentState; %>


    </script>

    <nav class="navbar navbar-default" role="navigation">
      <ul class="nav navbar-nav nav-justified">
        <li><a  id="reg"  onclick="reg()">注册</a></li>
        <li><a  id="tbl"  onclick="tbl()">操作</a></li>
        <li><a  onclick="mod()">用户</a></li>
      </ul>
    </nav>
    <div style="padding: 0 auto;width: 500px; display: inline">
      <strong style="text-align: left">current user: </strong>
      <strong style="text-align: left" id="username">none</strong>
    </div>

    <hr>


    <!--    修改信息    -->
    <form hidden=true id="mod-form"  action="index0" method="post">
      <strong style="padding-left: 0;text-align: left;">write your new information.</strong>
      <div class="form-group">
        <label for="mod-name">user name</label>
        <input type="text" class="form-control" id="mod-name" >
      </div>
      <div class="form-group">
        <label for="mod-nname">new user name</label>
        <input type="text" class="form-control" id="mod-nname" >
      </div>
      <div class="form-group">
        <label for="mod-pwd">password</label>
        <input type="password" class="form-control" id="mod-pwd" >
      </div>
      <div class="form-group">
        <label for="new-mod-pwd">new password</label>
        <input type="password" class="form-control" id="new-mod-pwd" >
      </div>


      <div class="form-group">
        <label class="radio-inline">
          <input type="radio" id="update-radio-male" value="0"> male
        </label>
        <label class="radio-inline">
          <input type="radio" id="update-radio-female" value="0"> female
        </label>

      </div>
      <strong style="padding-left: 0;text-align: left;">table to administrate:</strong>
      <div class="form-group" id="mod-tables">
        <label class="checkbox-inline">
          <input type="checkbox"> table1
        </label>
        <label class="checkbox-inline">
          <input type="checkbox"> table2
        </label>
      </div>
      <button type="submit" class="btn btn-primary">submit</button>

        <div hidden=true>
            <label for="logout" hidden=true>logout</label>
            <input type="text" class="form-control" id="logout"  >
        </div>


      <button type="submit" class="btn btn-primary" onclick="change_user()">change user</button>
    </form>




<!--    查看表格    -->
<form hidden=true id="mni-form"  action="index0" method="post" class="form-horizontal">
      <div class="form-group">
          <label for="table-req" class="col-md-2" style="text-align: left; padding-left: 0">查询信息:<a data-toggle="modal"  data-target="#myModal" style="text-align: left; padding: 0;">（什么是规范？)</a></label>
          <div class="col-12">
            <input type="text" class="form-control" id="table-req" placeholder="请按规范填写" style="width: 500px">
          </div>
                <div class="modal fade" id="myModal" style="">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-header"><strong style="text-align: left; padding-left: 0">以下的格式语法均遵从Java正则</strong></div>
                        <div class="modal-content">查找的格式：(找|查)(((危险等级|仓描述|操作员的登录号|操作员真实姓名管辖部门|产品类id|产品id|产品名|单位价格|存放费用原地址|目标地址|描述|指令类型|操作员id|操作时间|操作类型|货描述|操作号|货号)(是(.*?)|小于(.*)|大于(.*)|有规定)的)((危险等级|产品id|仓描述|操作员的登录号|操作员真实姓名管辖部门|产品名|单位价格|存放费用原地址|目标地址|描述|指令类型|操作员id|操作时间|操作类型|货描述|操作号|货号)(是(.*?)|小于(.*)|大于(.*)|有规定)的)?((危险等级|操作员id|产品id|仓描述|操作员的登录号|操作员真实姓名管辖部门|描述|产品类id|产品名|单位价格|存放费用原地址|目标地址|指令类型|操作号|操作时间|操作类型|货描述|货号)(是(.*?)|小于(.*)|大于(.*)|有规定)的)?((今天|昨天|前\\d+(天|星期|周)|\\d{4}/\\d{2}/\\d{2})的)?)【tables】</div>
                        <div class="modal-content">
                            增删的格式：((增加|增添|添加|加)|(删除|丢弃))(产品|(类别|类型)|货架|(操作员|人员|仓管员))
                        </div>
                        <div class="modal-content">
                            修改的格式：(受(委托|指令))?(把|将|从)(货号\d+|.*?仓库.*?单元|产品id\d+)((运到|转到|移到)(货号\d+|.*?仓库.*?单元)|原地处理.*)
                        </div>
                        <div class="modal-content">
                            如果不能查到任何数据，则有可能是格式不对，也有可能是数据库没有这样的数据，也可能是当前用户没有访问涉及到的表的权利。
                        </div>

                    </div>
                </div>

        <br>
      </div>


    <div id="req-alt" class="form-group">
        <button type="submit" class="btn btn-primary" >submit</button>
        <p id="table-content"><%=UserServlet.tblcontent%></p>
    </div>

</form>

<%--增添/删除表--%>
<form class="form-inline" id="alt-form" hidden=true action="index0" method="post">
    <div id="product" hidden=true>
        <div class="form-group">
            <label for="pid">产品id</label>
            <input type="text" class="form-control" id="pid"
                   placeholder="you@me.com">
        </div>
        <div class="form-group" >
            <label for="pname">产品名</label>
            <input type="text" class="form-control" id="pname" >
        </div>
        <div class="form-group">
            <label for="pcid">产品类id</label>
            <input type="text" class="form-control" id="pcid" >
        </div>
        <div class="form-group">
            <label for="specs">产品特性</label>
            <input type="text" class="form-control" id="specs">
        </div>
        <div class="form-group">
            <label for="price">单位价格</label>
            <input type="text" class="form-control" id="price" >
        </div>
        <div class="form-group">
            <label for="danger_class">危险等级</label>
            <input type="text" class="form-control" id="danger_class" >
        </div>
        <div class="form-group">
            <label for="transpt_reqrmts">运输要求</label>
            <input type="text" class="form-control" id="transpt_reqrmts" >
        </div>
        <div class="form-group">
            <label for="price_flag">存放费用标志</label>
            <input type="text" class="form-control" id="price_flag" >
        </div>
        <div class="form-group">
            <label for="description">描述</label>
            <input type="text" class="form-control" id="description" >
        </div>
        <button type="submit" class="btn btn-primary">提交</button>
    </div>
    <div id="product_class" hidden=true>
        <div class="form-group">
            <label for="pcid0">类id</label>
            <input type="text" class="form-control" id="pcid0" >
        </div>
        <div class="form-group">
            <label for="pcname">类名</label>
            <input type="text" class="form-control" id="pcname" >
        </div>
        <div class="form-group">
            <label for="dept">管辖部门</label>
            <input type="text" class="form-control" id="dept" >
        </div>
        <div class="form-group">
            <label for="description0">描述</label>
            <input type="text" class="form-control" id="description0" >
        </div>
        <button type="submit" class="btn btn-primary">提交</button>
    </div>
    <div id="repo" hidden=true>
        <div class="form-group">
            <label for="rid">货号</label>
            <input type="text" class="form-control" id="rid" >
        </div>
        <div class="form-group">
            <label for="repo_name">仓库</label>
            <input type="text" class="form-control" id="repo_name" >
        </div>
        <div class="form-group">
            <label for="runit">单元</label>
            <input type="text" class="form-control" id="runit" >
        </div>
        <div class="form-group">
            <label for="pid1">存放的产品id</label>
            <input type="text" class="form-control" id="pid1" >
        </div>
        <div class="form-group">
            <label for="orid">常驻操作员id</label>
            <input type="text" class="form-control" id="orid" >
        </div>
        <div class="form-group">
            <label for="danger_class1">危险承载登记</label>
            <input type="text" class="form-control" id="danger_class1" >
        </div>
        <div class="form-group">
            <label for="description1">描述</label>
            <input type="text" class="form-control" id="description1" >
        </div>

        <button type="submit" class="btn btn-primary">提交</button>

    </div>

    <div id="operator" hidden=true >
        <div class="form-group">
            <label for="orid2">操作员id</label>
            <input type="text" class="form-control" id="orid2" >
        </div>
        <div class="form-group">
            <label for="orname">操作员真实姓名</label>
            <input type="text" class="form-control" id="orname" >
        </div>
        <div class="form-group">
            <label for="login_name2">操作员的登录号</label>
            <input type="text" class="form-control" id="login_name2" >
        </div>
        <div class="form-group">
            <label for="description2">在职情况描述</label>
            <input type="text" class="form-control" id="description2" >
        </div>
        <button type="submit" class="btn btn-primary">提交</button>
    </div>


</form>


<!--    登录    -->


  <form hidden=true id="login" action="index0" method="post">
    <strong style="padding-left: 0;text-align: left;">please login.</strong>
    <div class="form-group">
      <label for="login-name">user name</label>
      <input type="text" class="form-control" id="login-name"   name="login-name">
    </div>
    <div class="form-group">
      <label for="login-pwd">password</label>
      <input type="password" class="form-control" id="login-pwd"    name="login-pwd">
    </div>
    <button type="submit" class="btn btn-primary">submit</button>
  </form>


<!--    注册    -->
  <form hidden=true id="reg-form"  action="index0" method="post">
    <div class="form-group">
      <label for="reg-name">user name</label>
      <input type="text" class="form-control" id="reg-name" >
    </div>
    <div class="form-group">
      <label for="reg-pwd">password</label>
      <input type="password" class="form-control" id="reg-pwd" >
    </div>


    <div class="form-group">
      <label class="radio-inline">
        <input type="radio" id="radio-male" value="0"> male
      </label>
      <label class="radio-inline">
        <input type="radio" id="radio-female" value="0"> female
      </label>
      
    </div>
    <strong style="padding-left: 0;text-align: left;">table to administrate:</strong>
    <div class="form-group" id="reg-tables">



      <label class="checkbox-inline" for="checkbox-table1">
        <input type="checkbox"  name="checkbox-table1" id="checkbox-table1"  value="0"> table1
      </label>
      <label class="checkbox-inline" for="checkbox-table2">
        <input type="checkbox" name="checkbox-table2" id="checkbox-table2" value="0"> table2
      </label>
    </div>
    <button type="submit" class="btn btn-primary">submit</button>
  </form>


  </body>
</html>
