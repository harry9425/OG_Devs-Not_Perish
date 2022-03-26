package com.harry9425.notperish;

import java.util.Comparator;
import java.util.Date;

public class allfoodsorter implements Comparator<productmodel>
{
    @Override
    public int compare(productmodel o1, productmodel o2) {
        Date c1 = new Date(Long.parseLong(o1.getExpiryinms()));
        Date t = new Date(System.currentTimeMillis());
        Date c2 = new Date(Long.parseLong(o2.getExpiryinms()));
        o1.setExrem((c1.getTime()-t.getTime())/1000);
        o2.setExrem((c2.getTime()-t.getTime())/1000);
        //o1.setName(o1.getExrem()+"");
        //o2.setName(o2.getExrem()+"");
        return o1.getExrem().compareTo(o2.getExrem());
    }
}