package servlets;

import bean.repmgr;
import services.UserServices;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

interface Ithis{
    String str(String sn, String sc);
}

public class UserServlet extends javax.servlet.http.HttpServlet {

    public static String regName = "none";
    public static int currentState;
    public static int lastState;

    public static List<List<String>> tblcontent = new ArrayList<>();

    public static final int SUCCESS=0, WRONG_NAME=1, WRONG_PASSWORD=2, OTHER=3;

    public static int requestStatus = OTHER;


    Ithis sqlseq = (String sn, String sc)-> sc.equals("''")?"":" and "+sn+"="+sc+"";


    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        /*
         * after examination, the return of request.getParameter("reg-name") is "" if nothing was inputted.
         */

        //register
        if (request.getParameter("reg-name")!=null&&request.getParameter("reg-pwd")!=null&&!request.getParameter("reg-name").equals("") && !request.getParameter("reg-pwd").equals("")){
            String reg_name = request.getParameter("reg-name");
            String reg_pwd = request.getParameter("reg-pwd");

            /*
                getParameter can return a value only if "for" in "label" is specified.
             */

            int checkbox_table_num = UserServices.showTables().size();
            String reg_male =request.getParameter("radio-male");
            String reg_female =request.getParameter("radio-female");

            ArrayList<String> tables = new ArrayList<>();

            for (int i = 0; i < checkbox_table_num; i++) {
                String tblname = "reg-checkbox-table"+i;
                tables.add(request.getParameter(tblname));
            }

//            System.out.println(tables);

            boolean sex = false;
            if (reg_male!=null)sex=true;

            if (UserServices.getUsers().contains(reg_name) || reg_name.equals("none")){
                requestStatus = WRONG_NAME;
                request.setAttribute("requestStatus",WRONG_NAME);
            }else {
                UserServices.createUser(new repmgr(reg_name,sex,reg_pwd,tables));
                requestStatus = SUCCESS;
                request.setAttribute("requestStatus",SUCCESS);
            }

            request.getRequestDispatcher("index.jsp").forward(request,response);
        }

        //login

        if (request.getParameter("login-name")!=null&&request.getParameter("login-pwd")!=null&&!request.getParameter("login-name").equals("") && !request.getParameter("login-pwd").equals("")){
            String login_name = request.getParameter("login-name");
            String login_pwd = request.getParameter("login-pwd");

            if (UserServices.getUsers().contains(login_name)){
                if (UserServices.checkPwd(login_name,login_pwd)){
                    regName = login_name;
                    requestStatus = SUCCESS;

                    request.setAttribute("reqName", login_name);
                    request.setAttribute("requestStatus",SUCCESS);
                }else {
                    requestStatus = WRONG_PASSWORD;
                    request.setAttribute("requestStatus",WRONG_PASSWORD);
                }

            }else {
                requestStatus = WRONG_NAME;
                request.setAttribute("requestStatus",WRONG_NAME);
            }

            request.getRequestDispatcher("index.jsp").forward(request,response);
        }


        //lookup tables

        if (request.getParameter("table-req")!=null){



            request.setCharacterEncoding("utf-8");

            String words = new String(request.getParameter("table-req").getBytes("ISO8859-1"), StandardCharsets.UTF_8);

            List<List<String>> lists = UserServices.getViewfromWords(regName, words);

            try {
                if ((lists != null ? lists.size() : 0) >0 && lists.get(0).size()>0 && lists.get(0).get(0).equals("#")){
                    request.setAttribute("form_to_show", /*lists.get(0).get(1)+*/lists.get(0).get(2));
                    tblcontent = lists;

                }else if ((lists != null ? lists.size() : 0) >0 && lists.get(0).size()>0 && lists.get(0).get(0).equals("<->")){

                    if (lists.get(1).size()==0){

                        request.setAttribute("tableChanged", lists.get(2));
                        tblcontent.set(0,lists.get(2));
                    }else {

//                        tblcontent.clear();
                        tblcontent.add(new ArrayList<>());
                        tblcontent.set(0,lists.get(3));

                        StringBuilder faults = new StringBuilder();

                        String preCode = lists.get(0).get(1);
                        for (String sufCode :
                                lists.get(1)) {
                            String s = preCode+sufCode;
                            switch (s) {
                                case "11":
                                    faults.append("产品id不存在，");
                                    break;
                                case "12":
                                    faults.append("货号或者仓库单元不存在，");
                                    break;
                                case "13":
                                    faults.append("货号或者仓库单元被占用，");
                                    break;
                                case "21":
                                    faults.append("出库的货号或者仓库单元不存在，");
                                    break;
                                case "22":
                                    faults.append("出库的货号或者仓库单元没有货物，");
                                    break;
                                case "23":
                                    faults.append("入库的货号或者仓库单元不存在，");
                                    break;
                                case "24":
                                    faults.append("入库的货号或者仓库单元被占用，");
                                    break;
                                case "31":
                                    faults.append("货号或者仓库单元不存在，");
                                    break;
                                case "32":
                                    faults.append("货号或者仓库单元没有货物，");
                                    break;
                            }
                        }

                        request.setAttribute("faults", faults);


//                        tblcontent.add(new ArrayList<>());
//
//                        String preCode = lists.get(0).get(1);
//                        for (String sufCode :
//                                lists.get(1)) {
//                            tblcontent.get(0).add(preCode+sufCode);
//                        }
                    }

                }else {
                    tblcontent = lists;
                    response.sendRedirect("index.jsp");
                    return;
                }
            } catch (NullPointerException e) {

                request.setAttribute("form_to_show", "!not_formatted");

                e.printStackTrace();
            }
//

//
//            int checkbox_table_num = UserServices.showTables().size();
//            ArrayList<String> tables = new ArrayList<>();
//
//            for (int i = 0; i < checkbox_table_num; i++) {
//                String tblname = "req-checkbox-table"+i;
////                if (tblname==null||tblname.equals("null")||tblname.equals(""))continue;
//                tables.add(request.getParameter(tblname));
//            }
//
//            tblcontent = new ArrayList<>(checkbox_table_num);
//            for (int i = 0; i < tblcontent.size(); i++) {
//                try {
////                    tblcontent.set(i,UserServices.getView(regName, tables.get(i)==null?"":UserServices.showTables().get(i)).toString());
//
//                }catch (NullPointerException e){
//                    requestStatus = WRONG_NAME;
//                    request.setAttribute("requestStatus",WRONG_NAME);
//                }
//
//            }
//            requestStatus = SUCCESS;
//            request.setAttribute("requestStatus",SUCCESS);
//
//            request.setAttribute("tables",tblcontent);
//
//            request.getRequestDispatcher("index.jsp").forward(request,response);

            //if response.sendRedirect("index.jsp"); is already executed then forward is not allowed.
            request.getRequestDispatcher("index.jsp").forward(request,response);
        }

        if (tblcontent != null && tblcontent.size()>0 && tblcontent.get(0).size()>0 && tblcontent.get(0).get(0).equals("#")){
            if (tblcontent.get(0).get(1).equals("+")){
                if (request.getParameter("pid")!=null && !request.getParameter("pid").equals("")){
                    String  pid =            "'"+request.getParameter("pid")             +"'",
                            pcid =          "'"+request.getParameter("pcid")            +"'",
                            pname =         "'"+request.getParameter("pname")           +"'",
                            specs =         "'"+request.getParameter("specs")           +"'",
                            price =         "'"+request.getParameter("price")           +"'",
                            price_flag =    "'"+request.getParameter("price_flag")      +"'",
                            danger_class =  "'"+request.getParameter("danger_class")    +"'",
                            description =   "'"+request.getParameter("description")     +"'",
                            transpt_reqrmts="'"+request.getParameter("transpt_reqrmts") +"'";

                    pid =new String(pid.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    pcid =new String(pcid.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    pname =new String(pname.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    specs =new String(specs.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    price =new String(price.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    price_flag =new String(price_flag.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    danger_class =new String(danger_class.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    description =new String(description.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    transpt_reqrmts=new String(transpt_reqrmts.getBytes("ISO8859-1"),StandardCharsets.UTF_8);


                    int re = UserServices.insertTable("product(pid,pcid,pname,specs,price,price_flag,danger_class,description,transpt_reqrmts)",
                            pid+","+pcid+","+pname+","+specs+","+price+","+price_flag+","+danger_class+","+description+","+transpt_reqrmts);

                    if (re<=0)
                        request.setAttribute("update_reply","insert");
//                    System.out.println(request.getParameter("pid"));
                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }
                else if (request.getParameter("pcid0")!=null&& !request.getParameter("pcid0").equals("")){
                    String pcid0 =          "'"+request.getParameter("pcid0")+"'";
                    String pcname =         "'"+request.getParameter("pcname")+"'";
                    String dept =           "'"+request.getParameter("dept")+"'";
                    String description0 =   "'"+request.getParameter("description0")+"'";

                    pcid0 =new String(pcid0.getBytes("ISO8859-1")    , StandardCharsets.UTF_8);
                    pcname =new String(pcname.getBytes("ISO8859-1")   , StandardCharsets.UTF_8);
                    dept =new String(dept.getBytes("ISO8859-1")     , StandardCharsets.UTF_8);
                    description0 =new String(description0.getBytes("ISO8859-1"), StandardCharsets.UTF_8);

                    int re = UserServices.insertTable("product_class(pcid,pcname,dept,description)", pcid0+","+pcname+","+dept+","+description0);

                    if (re<=0)
                        request.setAttribute("update_reply","insert");
                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }else if (request.getParameter("rid")!=null && !request.getParameter("rid").equals("")){
                    String rid =            "'"+request.getParameter("rid")+"'";
                    String repo_name =      "'"+request.getParameter("repo_name")+"'";
                    String runit =          "'"+request.getParameter("runit")+"'";
                    String pid1 =           "'"+request.getParameter("pid1")+"'";
                    String orid =           "'"+request.getParameter("orid")+"'";
                    String danger_class1 =  "'"+request.getParameter("danger_class1")+"'";
                    String description1 =   "'"+request.getParameter("description1")+"'";

                    rid = new String(rid.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    repo_name = new String(repo_name.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    runit = new String(runit.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    pid1 = new String(pid1.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    orid = new String(orid.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    danger_class1 = new String(danger_class1.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    description1 = new String(description1.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    int re = UserServices.insertTable("repo(rid,repo_name,runit,pid,orid,danger_class1,description)", rid+","+repo_name+","+runit+","+pid1+","+orid+","+danger_class1+","+description1);

                    if (re<=0)
                        request.setAttribute("update_reply","insert");
                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }else if (request.getParameter("orid2")!=null && !request.getParameter("orid2").equals("")){

                    String orid2 =          "'"+request.getParameter("orid2")+"'";
                    String orname =         "'"+request.getParameter("orname")+"'";
                    String login_name2 =    "'"+request.getParameter("login_name2")+"'";
                    String description2 =   "'"+request.getParameter("description2")+"'";

                    orid2 =new String(orid2.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    orname =new String(orname.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    login_name2 =new String(login_name2.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    description2 = new String(description2.getBytes("ISO8859-1"), StandardCharsets.UTF_8);

                    int re = UserServices.insertTable("operator(orid,orname,login_name,description)", orid2+","+orname+","+login_name2+","+description2);
                    if (re<=0)
                        request.setAttribute("update_reply","insert");
                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }
            }
            else if (tblcontent.get(0).get(1).equals("-")){
                if (request.getParameter("pid")!=null && !request.getParameter("pid").equals("")){
                    String  pid =            "'"+request.getParameter("pid")             +"'",
                            pcid =          "'"+request.getParameter("pcid")            +"'",
                            pname =         "'"+request.getParameter("pname")           +"'",
                            specs =         "'"+request.getParameter("specs")           +"'",
                            price =         "'"+request.getParameter("price")           +"'",
                            price_flag =    "'"+request.getParameter("price_flag")      +"'",
                            danger_class =  "'"+request.getParameter("danger_class")    +"'",
                            description =   "'"+request.getParameter("description")     +"'",
                            transpt_reqrmts="'"+request.getParameter("transpt_reqrmts") +"'";

                    pid =new String(pid.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    pcid =new String(pcid.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    pname =new String(pname.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    specs =new String(specs.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    price =new String(price.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    price_flag =new String(price_flag.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    danger_class =new String(danger_class.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    description =new String(description.getBytes("ISO8859-1"),StandardCharsets.UTF_8);
                    transpt_reqrmts=new String(transpt_reqrmts.getBytes("ISO8859-1"),StandardCharsets.UTF_8);





                    int re = UserServices.deleteTable("product",
                            (pid.equals("''")?"":("pid="+pid+""))+
                                    (pcid.equals("''")?"":(" and pcid="+pcid+""))+
                                    (pname.equals("''")?"":(" and pname="+pname+""))+
                                    (specs.equals("''")?"":(" and specs="+specs+""))+
                                    (price.equals("''")?"":(" and price="+price+""))+
                                    (price_flag.equals("''")?"":(" and price_flag="+price_flag+""))+
                                    (danger_class.equals("''")?"":(" and danger_class="+danger_class+""))+
                                    (description.equals("''")?"":(" and description="+description+""))+
                                    (transpt_reqrmts.equals("''")?"":(" and transpt_reqrmts="+transpt_reqrmts+""))


                    );
                    if (re<=0)
                        request.setAttribute("update_reply","delete");

//                    System.out.println(request.getParameter("pid"));
                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }else if (request.getParameter("pcid0")!=null&& !request.getParameter("pcid0").equals("")){
                    String pcid0 =          "'"+request.getParameter("pcid0")+"'";
                    String pcname =         "'"+request.getParameter("pcname")+"'";
                    String dept =           "'"+request.getParameter("dept")+"'";
                    String description0 =   "'"+request.getParameter("description0")+"'";

                    pcid0 =new String(pcid0.getBytes("ISO8859-1")    , StandardCharsets.UTF_8);
                    pcname =new String(pcname.getBytes("ISO8859-1")   , StandardCharsets.UTF_8);
                    dept =new String(dept.getBytes("ISO8859-1")     , StandardCharsets.UTF_8);
                    description0 =new String(description0.getBytes("ISO8859-1"), StandardCharsets.UTF_8);

                    int re = UserServices.deleteTable("product_class",
                            (pcid0.equals("''")?"":("pcid="+pcid0+""))+
                                    sqlseq.str("pcname", pcname)+
                                    sqlseq.str("dept", dept)+
                                    sqlseq.str("description",description0)
                            );
                    if (re<=0)
                        request.setAttribute("update_reply","delete");

                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }else if (request.getParameter("rid")!=null && !request.getParameter("rid").equals("")){
                    String rid =            "'"+request.getParameter("rid")+"'";
                    String repo_name =      "'"+request.getParameter("repo_name")+"'";
                    String runit =          "'"+request.getParameter("runit")+"'";
                    String pid1 =           "'"+request.getParameter("pid1")+"'";
                    String orid =           "'"+request.getParameter("orid")+"'";
                    String danger_class1 =  "'"+request.getParameter("danger_class1")+"'";
                    String description1 =   "'"+request.getParameter("description1")+"'";

                    rid = new String(rid.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    repo_name = new String(repo_name.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    runit = new String(runit.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    pid1 = new String(pid1.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    orid = new String(orid.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    danger_class1 = new String(danger_class1.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    description1 = new String(description1.getBytes("ISO8859-1"), StandardCharsets.UTF_8);

                    int re = UserServices.deleteTable("repo",
                            (rid.equals("''")?"":("rid="+rid))+
                                    sqlseq.str("repo_name", repo_name)+
                                    sqlseq.str("runit", runit)+
                                    sqlseq.str("pid1", pid1)+
                                    sqlseq.str("orid", orid)+
                                    sqlseq.str("danger_class1", danger_class1)+
                                    sqlseq.str("description1", description1)
                    );
                    if (re<=0)
                        request.setAttribute("update_reply","delete");

                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }else if (request.getParameter("orid2")!=null && !request.getParameter("orid2").equals("")){

                    String orid2 =          "'"+request.getParameter("orid2")+"'";
                    String orname =         "'"+request.getParameter("orname")+"'";
                    String login_name2 =    "'"+request.getParameter("login_name2")+"'";
                    String description2 =   "'"+request.getParameter("description2")+"'";

                    orid2 =new String(orid2.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    orname =new String(orname.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    login_name2 =new String(login_name2.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
                    description2 = new String(description2.getBytes("ISO8859-1"), StandardCharsets.UTF_8);


                    int re = UserServices.deleteTable("operator",
                                orid2.equals("''")?"":("orid2="+orid2)+
                                        sqlseq.str("orname",orname)+
                                        sqlseq.str("login_name2",login_name2)+
                                        sqlseq.str("description2",description2)
                    );
                    if (re<=0)
                        request.setAttribute("update_reply","delete");

                    request.getRequestDispatcher("index.jsp").forward(request,response);
                }
            }
        }





        if (request.getParameter("logout")!=null){
            requestStatus = SUCCESS;
            tblcontent = null;
            regName = "none";
            response.sendRedirect("index.jsp");
        }


    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        System.out.println("getting");

//        response.sendRedirect("http://localhost:8080/Project05_2/");
    }
}
