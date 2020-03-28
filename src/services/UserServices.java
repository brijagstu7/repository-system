package services;

import bean.repmgr;
import db.DButils;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServices {

    private static Connection conn = null;
    private static PreparedStatement pstmt = null;
    private static ResultSet rs = null;
    private static HashMap<String, String> nameMap = new HashMap<>(), rnameMap = new HashMap<>();

    static {
        conn = DButils.getConn();
        nameMap.put("operation", "操作");
        nameMap.put("operator", "操作员");
        nameMap.put("product", "产品");
        nameMap.put("product_class", "产品类");
        nameMap.put("repo", "货架");
        nameMap.put("product_progress", "产品进度");

        nameMap.put("pid","产品id");
        nameMap.put("pname","产品名");
        nameMap.put("pcid","产品类id");
        nameMap.put("specs","产品特性");
        nameMap.put("price","单位价格");
        nameMap.put("danger_class","危险等级");
        nameMap.put("transpt_reqrmts","运输要求");
        nameMap.put("price_flag","存放费用标志");
        nameMap.put("description","描述");

        nameMap.put("oid","操作号");
        nameMap.put("otype","操作类型");
        nameMap.put("orid","操作员id");
        nameMap.put("otime","操作时间");
        nameMap.put("rid","货号");
        nameMap.put("addr_from","原地址");
        nameMap.put("addr_to","目标地址");
        nameMap.put("instruction","指令类型");
//        nameMap.put("description","描述");

//        nameMap.put("rid","货号");
//        nameMap.put("oid","操作号");

//        nameMap.put("pcid","类id");
        nameMap.put("pcname","类名");
        nameMap.put("dept","管辖部门");
//        nameMap.put("","描述");

//        nameMap.put("","货号");
        nameMap.put("repo_name","仓库");
        nameMap.put("runit","单元");
//        nameMap.put("pid","存放的产品id");
//        nameMap.put("rid","常驻操作员id");
//        nameMap.put("danger_class","危险承载登记");
//        nameMap.put("","描述");

//        nameMap.put("orid","操作员id");
        nameMap.put("orname","操作员真实姓名");
        nameMap.put("login_name","操作员的登录号");
//        nameMap.put("","在职情况描述");

         nameMap.put("name","操作员登录号");
         nameMap.put("pwd","密码");
         nameMap.put("sex","性别");
         nameMap.put("tables","权限列表");


        nameMap.forEach((k,v)->{
            rnameMap.put(v,k);
        });

    }



    public static boolean createUser(repmgr r) {

        boolean ok = false;

        String sql = "select name from users where name="+r.getName()+";";

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()){

                //cannot create
                System.err.println("msg: a user with name "+r.getName()+" already exists.");
                //DButils.close(rs,pstmt,conn);

            }else {

                sql = "insert into users values ("+r.toString()+");";
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();

                ok = true;
                //DButils.close(rs,pstmt,conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return ok;
    }

    public static boolean modifyUser(repmgr original, repmgr latest){
        boolean ok = false;

        String sql = "select name from users where name="+original.getName()+";";

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (!rs.next()){
                System.err.println("msg: a user named "+original.getName()+" does not exist, so cannot be modified.");
                //DButils.close(rs,pstmt,conn);
            }else {
                sql = "delete from users where name="+original.getName()+";";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
                sql = "insert into users (name, sex, pwd, tables) values ("+latest.toString()+");";
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();
                ok = true;
                //DButils.close(rs,pstmt,conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ok;
    }

    /**
     *
     * @param name user name
     * @param table table to be viewed
     *  typeSeq   specifies types of table in order.
     *                  go to see java.sql.Types
     *          get column's type with ResultSetMetaData.
     *
     *
     * @return if name is not existed or has no right to access table, then null; otherwise the tables collected.
     */
    public static List<List<String>> getView(String name, String table, String tblCond){
        if (table.equals(""))return null;
        String sql = "select * from users where name='"+name+"';";

        List<List<String>> lists = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            /*
            get column's type with ResultSetMetaData.
             */

            ResultSetMetaData rdt = pstmt.getMetaData();
            List<Integer> typeSeq = new ArrayList<>();
            int ind=1;
            while (ind>0){
                try {
                    typeSeq.add(rdt.getColumnType(ind++));
                }catch (SQLException e){
                    ind=-1;
                }

            }

            if (!rs.next()){
                System.err.println("msg: cannot get table with a non-existed user name.");
                //DButils.close(rs,pstmt,conn);
                return null;

            }else {
                String tables = rs.getString("tables");
                Scanner sc = new Scanner(tables);
                sc.useDelimiter("[, ]");
                boolean canView = false;
                while (sc.hasNext()){
                    if (sc.next().equals(table))
                        canView = true;
                }
                if (canView){
                    sql = "select * from "+table;
                    if (!tblCond.equals("")){
                        sql += " where "+tblCond+";";
                    }
                    pstmt = conn.prepareStatement(sql);
                    rs = pstmt.executeQuery();

                    while (rs.next()){
                        lists.add(new ArrayList<>());
                        List<String> list = lists.get(lists.size()-1);

                        for (int i = 1; i < typeSeq.size(); i++) {
                            String s = null;
                            try {
                                s = rs.getString(i);
                            }catch (SQLException e) {
//                                e.printStackTrace();
                                continue;
                            }

                            if (s==null || s.equals("null")|| s.equals(""))continue;
                            list.add(s);
                        }

                    }
                    return lists;
                }else {
                    System.err.println("msg: user "+name+" does not have access to table \'"+table+"\'.");
                    //DButils.close(rs,pstmt,conn);
                    return null;
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            return lists;
        }

    }

    public static List<List<String>> getView(String name, String table){
        return getView(name, table, "");
    }

    public static List<String> showTables(){
        String sql = "show tables";
        List<String> list = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            list = new ArrayList<>();
            while (rs.next()){
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getUsers(){
        String sql = "select name from users";
        List<String> list = null;

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            list = new ArrayList<>();
            while (rs.next()){
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean checkPwd(String name, String pwd){
        String sql = "select pwd from users where name=\'"+name+"\'";
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getString(1).equals(pwd))return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * "捕获组是通过从左至右计算其开括号来编号。例如，在表达式（（A）（B（C））），有四个这样的组：
     * ((A)(B(C)))
     * (A)
     * (B(C))
     * (C)
     * "
     *
     * not defined addr_from and addr_to and description yet!!!!!!!!
     *
     * @param words a formatted string
     * @return null if the words are not formatted.
     */
    public static List<List<String>> getViewfromWords(String user, String words){

        String sql = "";

        //the following code(from here to "tables.append(')');") is used to get all tables in database

        StringBuilder tables = new StringBuilder(showTables().toString().replaceAll("[\\[\\],]", ""));
        Scanner sc = new Scanner(tables.toString());
        tables = new StringBuilder();
        tables.append('(');
        while (sc.hasNext()){
            String name = nameMap.get(sc.next());
            if (name != null && !name.equals("")){
                tables.append(name).append("|");

            }
        }
        tables.deleteCharAt(tables.length()-1);
        tables.append(')');

        String pat;
        if (words.matches(pat="(找|查)(((危险等级|仓描述|操作员的登录号|操作员真实姓名管辖部门|产品类id|产品id|产品名|单位价格|存放费用原地址|目标地址|描述|指令类型|操作员id|操作时间|操作类型|货描述|操作号|货号)(是(.*?)|小于(.*)|大于(.*)|有规定)的)((危险等级|产品id|仓描述|操作员的登录号|操作员真实姓名管辖部门|产品名|单位价格|存放费用原地址|目标地址|描述|指令类型|操作员id|操作时间|操作类型|货描述|操作号|货号)(是(.*?)|小于(.*)|大于(.*)|有规定)的)?((危险等级|操作员id|产品id|仓描述|操作员的登录号|操作员真实姓名管辖部门|描述|产品类id" +
                "|产品名|单位价格|存放费用原地址|目标地址|指令类型|操作号|操作时间|操作类型|货描述|货号)(是(.*?)|小于(.*)|大于(.*)|有规定)的)?((今天|昨天|前\\d+(天|星期|周)|\\d{4}/\\d{2}/\\d{2})的)?)"+tables+"$")) {

            Pattern p = Pattern.compile(".*"+tables);
            Matcher m = p.matcher(words);

            if (m.find()){
                String tableName = m.group(1);
                StringBuilder tableAttr = new StringBuilder();

                sql = "select COLUMN_NAME from information_schema.COLUMNS where table_name=";
                Set<String> keys = nameMap.keySet();

                try {

                    for (String key :
                            keys) {
                        String sql_ = sql+"'"+key+"'";

                        pstmt = conn.prepareStatement(sql_);
                        rs = pstmt.executeQuery();

                        while (rs.next()){
                            tableAttr.append(nameMap.get(rs.getString(1)));
                            tableAttr.append('|');
                        }
                        tableAttr.deleteCharAt(tableAttr.length()-1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
//                pat = pat.replaceFirst("\\.\\*",tableAttr.toString());
//                pat = pat.replaceFirst("是\\(\\.\\*\\)", "是(.*lq)");
//                words = words.replaceFirst("的","lq的");
            }else {
                return null;
            }


            p = Pattern.compile(pat);
            m = p.matcher(words);

            List<String> eventStrs = new ArrayList<>();
            StringBuilder res = new StringBuilder();
            String time = null, tableName = null;
            if (m.find()){
                for (int i = 3; i <= m.groupCount(); i++) {
                    if (m.group(i) != null&&m.group(i).matches("(.*是.*的)|((.*)大于(.*)的)|((.*)小于(.*)的)")){
                        eventStrs.add(m.group(i));
                    }

                    res.append(m.group(i)).append(",");
                }

                time = m.group(m.groupCount()-2);
                tableName = m.group(m.groupCount());
            }




//            System.out.println(res);

            sql = "select * from "+tableName;
            assert tableName != null;

            String cond = "";
            if (tableName.equals("操作") && time!=null){

                long timeSecs = System.currentTimeMillis()/1000;
                long secsPerDay = 24*60*60;

                if (time.equals("今天")){

                    cond+= " otime<"+timeSecs+" and otime>"+(timeSecs-timeSecs%secsPerDay);
                } else if (time.equals("昨天")) {
                    cond+= " otime<"+(timeSecs-timeSecs%secsPerDay)+" and otime>"+(timeSecs-timeSecs%secsPerDay-secsPerDay);
                }else if (time.matches("前(\\d+)(天|星期|周)")){

                    p = Pattern.compile("前(\\d+)(天|星期|周)");
                    m = p.matcher(time);

                    int quan = 0;
                    String perDay = null;
                    if (m.find()){
                        quan = Integer.parseInt(m.group(1));
                        perDay = m.group(2);
                    }

                    //least bound
                    long lb = 0;

                    assert perDay != null;
                    switch (perDay){
                        case "天":lb = timeSecs-quan*secsPerDay;
                            break;
                        case "星期":
                        case "周":
                            lb = timeSecs-quan*7*secsPerDay;
                            break;

                    }

                    cond += " otime<"+timeSecs+" and otime>"+lb;

                }else if (time.matches("\\d{4}/\\d{2}/\\d{2}")){
//                    DateFormat.pa

                    Long time_ = timeToSecond(time);
                    time_ -= 8*60*60;//8:00 to 0:00

                    cond += " otime<"+(time_+secsPerDay)+" and otime>"+time_;
                }

//                cond += " otime='"+time+"' ";
            }

            cond+=time!=null?" and ":"";

            for (String patt :
                    eventStrs) {

                if (patt.matches("(.*)是(.*)的")) {
                    p = Pattern.compile("(.*)是(.*)的");
                    m = p.matcher(patt);

                    String key = null, value = null;
                    if (m.find()){
                        key = m.group(1);
                        value = m.group(2);
                    }


                    key = rnameMap.get(key);

                    cond += key+"='"+value+"' and ";
                } else if (patt.matches("(.*)小于(.*)的")){
                    p = Pattern.compile("(.*)小于(.*)的");
                    m = p.matcher(patt);

                    String key = null, value = null;
                    if (m.find()){
                        key = m.group(1);
                        value = m.group(2);
                    }


                    key = rnameMap.get(key);

                    cond += key+"<'"+value+"' and ";

                } else if (patt.matches("(.*)大于(.*)的")){
                    p = Pattern.compile("(.*)大于(.*)的");
                    m = p.matcher(patt);

                    String key = null, value = null;
                    if (m.find()){
                        key = m.group(1);
                        value = m.group(2);
                    }


                    key = rnameMap.get(key);

                    cond += key+">'"+value+"' and ";
                }

            }

            tableName = rnameMap.get(tableName);

            cond = cond.substring(0,cond.length()-4);

            return getView(user, tableName, cond);
        }
        else if (words.matches(pat="((增加|增添|添加|加)|(删除|丢弃))(产品|(类别|类型)|货架|(操作员|人员|仓管员))")){


            List<List<String>> lists = new ArrayList<>();
            lists.add(new ArrayList<String>(){
                {
                    add("#");// to indicate the condition of altering
                }
            });


            Pattern p = Pattern.compile(pat);
            Matcher m = p.matcher(words);

            if (m.find()){
                switch (m.group(1)){
                    case "增加":
                    case "添加":
                    case "加":
                    case "增添":
                        lists.get(0).add("+");
                        break;
                    case "删除":
                    case "丢弃":
                        lists.get(0).add("-");
                        break;
                }

                switch (m.group(4)){
                    case "产品":
                        lists.get(0).add("product");
                        break;
                    case "类别":
                    case "类型":
                        lists.get(0).add("product_class");
                        break;
                    case "货架":
                        lists.get(0).add("repo");
                        break;
                    case "操作员":
                    case "人员":
                    case "仓管员":

                        lists.get(0).add("operator");
                        break;
                }
            }


            return lists;

        }
        else if (words.matches(pat="(受(委托|指令))?(把|将|从)(货号\\d+|.*?仓库.*?单元|产品id\\d+)((运到|转到|移到)(货号\\d+|.*?仓库.*?单元)|原地处理.*)")){

            List<List<String>> lists = new ArrayList<List<String>>() {
                {
                    add(new ArrayList<>());
                    add(new ArrayList<>());
                    add(new ArrayList<>());
                    add(new ArrayList<>());
                }
            };
            lists.get(0).add("<->");

            long oid = System.currentTimeMillis()/1000;
            int otype;
            int orid = 0;
            long otime;
            int to_rid = 0;
            int from_rid = 0;
            int instruction;
            int rid = 0;
            int pid = 0;
            boolean io = false;//0 for in, 1 for out

            Pattern p = Pattern.compile(pat);
            Matcher m = p.matcher(words);

            if (m.find()){
                String isDelegated = m.group(1),
                        from = m.group(4),
                        io_ = m.group(3),
                        to = m.group(5);
                if (io_.equals("从")){
                    io = false;
                }else {
                    io = true;
                }

                if (from.matches("产品id\\d+") && to.matches("(运到|转到|移到)(货号\\d+|.*?仓库.*?单元)")){
                    lists.get(0).add("1");
                    otype = 1;

                    p = Pattern.compile("产品id(\\d+)");
                    m = p.matcher(from);

                    if (m.find()){

                        pid = Integer.parseInt(m.group(1));

                        sql = "select pid from product where pid='"+pid+"'";

                        try {
                            pstmt = conn.prepareStatement(sql);
                            rs = pstmt.executeQuery();

                            if (rs.next()){

                            }else {
                                lists.get(1).add("1");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    p = Pattern.compile("(运到|转到|移到)(货号(\\d+)|(.*?)仓库(.*?)单元)");
                    m = p.matcher(to);

                    if (m.find()){
                        String srid, repo_name, runit;
                        if (m.group(3)==null){//(.*?)仓库(.*?)单元
                            repo_name = m.group(4);
                            runit = m.group(5);
                            sql = "select rid from repo where repo_name='"+repo_name+"' and runit='"+runit+"'";
                        }else {//货号(\d+)
                            srid = m.group(3);
                            sql = "select rid from repo where rid='"+srid+"'";
                        }

                        try {
                            pstmt = conn.prepareStatement(sql);
                            rs = pstmt.executeQuery();

                            if (rs.next()) {
                                to_rid = rs.getInt(1);
                                if (rs.getString("rid") == null || rs.getString("rid").equals("") || rs.getString("rid").equals("null")) {
                                    lists.get(1).add("2");
                                } else {
                                    sql = "select pid from repo where rid='"+to_rid+"'";
                                    pstmt = conn.prepareStatement(sql);
                                    rs = pstmt.executeQuery();
                                    if (rs.next()){
                                        if (rs.getString(1)==null || rs.getString(1).equals("") || rs.getString(1).equals("null")){

                                        }else {
                                            lists.get(1).add("3");
                                            ResultSetMetaData md = rs.getMetaData();
                                            for (int i = 1; i <= 7; i++) {
                                                lists.get(3).add(md.getColumnName(i) + ":" + rs.getString(i));
                                            }

                                        }
                                    }

                                }
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    setOperationRepo(user, lists, oid, otype, orid, to_rid, from_rid, rid, pid, isDelegated);


                }
                else if (from.matches("货号\\d+|.*?仓库.*?单元") && to.matches("(运到|转到|移到)(货号\\d+|.*?仓库.*?单元)")){


                    otype = 3;
                    lists.get(0).add("2");

                    from_rid = checkFault12andReturnFrom_rid(lists, from_rid, from);
                    to_rid = checkFault34andReturnTo_rid(lists, to_rid, to);


                    setOperationRepo(user, lists, oid, otype, orid, to_rid, from_rid, rid, pid, isDelegated);


                }
                else if (from.matches("货号\\d+|.*?仓库.*?单元") && to.matches("原地处理.*")){


                    otype = 0;
                    lists.get(0).add("3");

                    from_rid = checkFault12andReturnFrom_rid(lists, from_rid, from);

                    setOperationRepo(user, lists, oid, otype, orid, to_rid, from_rid, rid, pid, isDelegated);

                }
                else {
                    return null;
                }


            }

            return lists;
        }
        return null;
    }

    private static int checkFault34andReturnTo_rid(List<List<String>> lists, int to_rid, String to) {
        Pattern p;
        Matcher m;
        String sql;
        p = Pattern.compile("(运到|转到|移到)货号(\\d+)|(.*?)仓库(.*?)单元");
        m = p.matcher(to);
        if (m.find()){
            if (m.group(2)==null){
                sql = "select rid from repo where repo_name='"+m.group(3)+"' and runit='"+m.group(4)+"'";
                try {
                    pstmt = conn.prepareStatement(sql);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        to_rid = rs.getInt(1);
                        sql = "select * from repo where repo_name='"+m.group(3)+"' and runit='"+m.group(4)+"'";
                        pstmt = conn.prepareStatement(sql);
                        rs = pstmt.executeQuery();
                        if (rs.getString("pid")!=null){
                            lists.get(1).add("4");

                            ResultSetMetaData md = rs.getMetaData();
                            for (int i = 1; i <= 9; i++) {
                                lists.get(3).add(md.getColumnName(i) + ":" + rs.getString(i));
                            }
                        }
                    }else {
                        lists.get(1).add("3");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {
                sql = "select rid from repo where rid='"+m.group(2)+"'";
                try {
                    pstmt = conn.prepareStatement(sql);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        to_rid = rs.getInt(1);
                        sql = "select * from repo where rid='"+m.group(2)+"'";
                        pstmt = conn.prepareStatement(sql);
                        rs = pstmt.executeQuery();
                        if (rs.getString(1)!=null){
                            lists.get(1).add("4");
                            ResultSetMetaData md = rs.getMetaData();
                            for (int i = 1; i <= 9; i++) {
                                lists.get(3).add(md.getColumnName(i) + ":" + rs.getString(i));
                            }
                        }
                    }else {
                        lists.get(1).add("3");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return to_rid;
    }

    private static int checkFault12andReturnFrom_rid(List<List<String>> lists, int from_rid, String from) {
        Pattern p;
        Matcher m;
        String sql;
        p = Pattern.compile("货号(\\d+)|(.*?)仓库(.*?)单元");
        m = p.matcher(from);


        if (m.find()){

            if (m.group(1)==null){
                sql = "select rid from repo where repo_name='"+m.group(2)+"' and runit='"+m.group(3)+"'";
                try {
                    pstmt = conn.prepareStatement(sql);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        from_rid = rs.getInt(1);
                        sql = "select * from repo where repo_name='"+m.group(2)+"' and runit='"+m.group(3)+"'";
                        pstmt = conn.prepareStatement(sql);
                        rs = pstmt.executeQuery();
                        if (rs.getString("pid")==null){
                            lists.get(1).add("2");

                            ResultSetMetaData md = rs.getMetaData();
                            for (int i = 1; i <= 9; i++) {
                                lists.get(3).add(md.getColumnName(i) + ":" + rs.getString(i));
                            }
                        }
                    }else {
                        lists.get(1).add("1");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {
                sql = "select rid from repo where rid='"+m.group(1)+"'";
                try {
                    pstmt = conn.prepareStatement(sql);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        from_rid = rs.getInt(1);
                        sql = "select * from repo where rid='"+m.group(1)+"'";
                        pstmt = conn.prepareStatement(sql);
                        rs = pstmt.executeQuery();
                        if (rs.getString("pid")==null){
                            lists.get(1).add("2");

                            ResultSetMetaData md = rs.getMetaData();
                            for (int i = 1; i <= 9; i++) {
                                lists.get(3).add(md.getColumnName(i) + ":" + rs.getString(i));
                            }
                        }
                    }else {
                        lists.get(1).add("1");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return from_rid;
    }

    /**
     *
     * @param user
     * @param lists
     * @param oid
     * @param otype
     * @param orid
     * @param to_rid
     * @param from_rid
     * @param rid
     * @param pid
     * @param isDelegated
     *
     * solves orid, otime, rid, instruction
     *
     * but not defined addr_from and addr_to yet
     */
    private static void setOperationRepo(String user, List<List<String>> lists, long oid, int otype, int orid, int to_rid, int from_rid, int rid, int pid, String isDelegated) {
        String sql;
        long otime;
        int instruction;
        if (lists.get(1).size()==0){// no fault

            sql = "select orid from operator where login_name='"+user+"'";

            try {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                if (rs.next()){

                    orid = rs.getInt(1);
                }else {
                    lists.get(1).add("non-matched login-name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            switch (otype){
                case 1:
                    rid = to_rid;
                    otime = oid;
                    break;

                case 3:
                    rid = to_rid;
                case 2:
                    otime = oid + 24*60*60 * 3;
                    break;
                case 0:
                    rid = from_rid;
                    otime = oid + 24*60*60;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value of otype: " + otype);
            }

            instruction = isDelegated==null?1:0;

            sql = "insert into operation (oid, otype, orid, otime, rid, addr_from, addr_to, instruction, description) values ('"+
                    oid+"','"+
                    otype+"','"+
                    orid+"','"+
                    otime+"','"+
                    rid+"','"+
                    "','"+// not defined addr_from and addr_to and description yet!!!!!!!!
                    "','"+
                    instruction+"','"+
                    "',"
                    +");";

            try {
                pstmt = conn.prepareStatement(sql);
                int re = pstmt.executeUpdate();

                if (re<=0){
                    lists.get(1).add("cannot modify table even with all attributes correct");
                }

                sql = "update repo set pid = "+pid+" where rid="+to_rid;
                pstmt = conn.prepareStatement(sql);
                re = pstmt.executeUpdate();

                if (re<=0){
                    lists.get(1).add("cannot modify table even with all attributes correct");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


            sql = "select * from operation where oid='"+oid+"'";
            try {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= 9; i++) {
                    lists.get(2).add(md.getColumnName(i)+":"+rs.getString(i));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static int insertTable(String table, String contents){
        String sql = "insert into "+table+" values ("+contents+")";
        int re = 0;
        try {
            pstmt = conn.prepareStatement(sql);
            re = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return re;
    }

    public static int deleteTable(String table, String cond){
        String sql = "delete from "+table;
        if (cond != null && !cond.equals("")){
            sql += " where "+cond;
        }
        int re = 0;
        try {
            pstmt = conn.prepareStatement(sql);
            re = pstmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return re;
    }
    /**
     * 将现在的正常字符串格式时间转换成距离1970早上8点整的数字时间
     * 比如字符串格式时间："2017/12/15"
     * 转换后的数字时间："1513345743"
     * @param time
     * @return
     */
    public static Long timeToSecond(String time){
        String dateStr="1970/1/1";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
        long aftertime=0;
        try {
            Object d1=sdf.parse(time).getTime();
            Date miDate = sdf.parse(dateStr);
            Object t1=miDate.getTime();
            long d1time=Long.parseLong(d1.toString())/1000;
            long t1time=Long.parseLong(t1.toString())/1000;
            aftertime = d1time-t1time;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return aftertime;

    }


    public static void main(String[] args) {

//        System.out.println(getViewfromWords("2","找操作号是111的操作类型是1的昨天的操作"));

        System.out.println(2147483646-System.currentTimeMillis()/1000);



        //after examination, found method getView() available.

    }
}
