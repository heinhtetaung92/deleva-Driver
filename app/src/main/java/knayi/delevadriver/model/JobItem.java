// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver.model;

import java.util.List;

// Referenced classes of package knayi.delevadriver.model:
//            Requester

public class JobItem
{

    private String _address;
    private String _createAt;
    private String _id;
    private long _lan;
    private long _lat;
    private List _pictures;
    private String _price;
    private Requester _requester;
    private String _secret_code;
    private String _status;
    private String _type;
    private String _v;

    public JobItem()
    {
    }

    public String get_address()
    {
        return _address;
    }

    public String get_createAt()
    {
        return _createAt;
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

    public List get_pictures()
    {
        return _pictures;
    }

    public String get_price()
    {
        return _price;
    }

    public Requester get_requester()
    {
        return _requester;
    }

    public String get_secret_code()
    {
        return _secret_code;
    }

    public String get_status()
    {
        return _status;
    }

    public String get_type()
    {
        return _type;
    }

    public String get_v()
    {
        return _v;
    }

    public void set_address(String s)
    {
        _address = s;
    }

    public void set_createAt(String s)
    {
        _createAt = s;
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

    public void set_pictures(List list)
    {
        _pictures = list;
    }

    public void set_price(String s)
    {
        _price = s;
    }

    public void set_requester(Requester requester)
    {
        _requester = requester;
    }

    public void set_secret_code(String s)
    {
        _secret_code = s;
    }

    public void set_status(String s)
    {
        _status = s;
    }

    public void set_type(String s)
    {
        _type = s;
    }

    public void set_v(String s)
    {
        _v = s;
    }
}
