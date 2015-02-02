// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver.model;

import java.io.Serializable;
import java.util.List;

public class Requester implements Serializable{

    private String _address;
    private String _business_type;
    private String _confirmed;
    private String _email;
    private String _hash;
    private String _id;
    private String _type;
    private long _lan;
    private long _lat;
    private String _mobile_number;
    private String _name;
    private List _pictures;
    private String _salt;
    private String _v;

    public Requester()
    {
    }

    public String get_address()
    {
        return _address;
    }

    public String get_business_type()
    {
        return _business_type;
    }

    public String get_confirmed()
    {
        return _confirmed;
    }

    public String get_email()
    {
        return _email;
    }

    public String get_hash()
    {
        return _hash;
    }

    public String get_id()
    {
        return _id;
    }

    public long get_lan()
    {
        return _lan;
    }

    public long get_lat()
    {
        return _lat;
    }

    public String get_mobile_number()
    {
        return _mobile_number;
    }

    public String get_name()
    {
        return _name;
    }

    public List get_pictures()
    {
        return _pictures;
    }

    public String get_salt()
    {
        return _salt;
    }

    public String get_v()
    {
        return _v;
    }

    public void set_address(String s)
    {
        _address = s;
    }

    public void set_business_type(String s)
    {
        _business_type = s;
    }

    public void set_confirmed(String s)
    {
        _confirmed = s;
    }

    public void set_email(String s)
    {
        _email = s;
    }

    public void set_hash(String s)
    {
        _hash = s;
    }

    public void set_id(String s)
    {
        _id = s;
    }

    public void set_latAndlan(long l, long l1)
    {
        _lat = l;
        _lan = l1;
    }

    public void set_mobile_number(String s)
    {
        _mobile_number = s;
    }

    public void set_name(String s)
    {
        _name = s;
    }

    public void set_pictures(List list)
    {
        _pictures = list;
    }

    public void set_salt(String s)
    {
        _salt = s;
    }

    public void set_v(String s)
    {
        _v = s;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }
}
