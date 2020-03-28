package bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class repmgr {

    private String name;
    private boolean sex;
    private String pwd;
    private List<String> tables;

    public repmgr(String name, boolean sex, String pwd, List<String> tables) {
        this.name = name;
        this.sex = sex;
        this.pwd = pwd;
        this.tables = tables;
    }

    public repmgr(String name, boolean sex, String pwd, String ts) {
        this.name = name;
        this.sex = sex;
        this.pwd = pwd;

        Scanner sc = new Scanner(ts);
        sc.useDelimiter("[, \t\n]");

        this.tables = new ArrayList<>();

        while (sc.hasNext()){
            tables.add(sc.next());
        }
    }

    public repmgr(String name, boolean sex, String pwd) {
        this.name = name;
        this.sex = sex;
        this.pwd = pwd;
    }

    public repmgr() {
    }

    public String getName() {
        return "\'"+name+"\'";
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex?1:0;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getPwd() {
        return "\'"+pwd+"\'";
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * For the use of mysql manipulations.
     * @return string in mysql grammar.
     */
    @Override
    public String toString() {
        String s=
               '\''+name +'\''+ ',' +'\''+
               sex +'\''+','+'\''+
                pwd +'\''+ ',' +'\''+
                 tables+'\'' ;

        s = s.replaceAll("[\\[\\]]","");
        s = s.replaceAll("true","1");
        s = s.replaceAll("false","0");
        return s;

    }

    public static void main(String[] args) {
        repmgr r = new repmgr("name",false,"123","table1,table2");

        System.out.println(r);
    }
}
