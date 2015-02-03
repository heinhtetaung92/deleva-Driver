// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package knayi.delevadriver.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

// Referenced classes of package knayi.delevadriver.model:
//            Requester

public class JobItem implements Parcelable{

    private String _address;
    private String _createAt;
    private String _id;
    private long _lon;
    private long _lat;
    private List _pictures;
    private int _price;
    private Requester _requester;
    private String _secret_code;
    private String _status;
    private String _type;

    public JobItem(){

    }

    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        // TODO Auto-generated method stub
        dest.writeString(_address);
        dest.writeString(_createAt);
        dest.writeString(_id);
        dest.writeLong(_lon);
        dest.writeLong(_lat);
        dest.writeList(_pictures);
        dest.writeInt(_price);
        dest.writeSerializable(_requester);
        dest.writeString(_secret_code);
        dest.writeString(_status);
        dest.writeString(_type);

    }
    public JobItem(Parcel in)
    {
        this._address = in.readString();
        this._createAt = in.readString();
        this._id = in.readString();
        this._lon = in.readLong();
        this._lat = in.readLong();
        in.readList(this._pictures, List.class.getClassLoader());
        this._price = in.readInt();
        this._requester = (Requester) in.readSerializable();
        this._secret_code = in.readString();
        this._status = in.readString();
        this._type = in.readString();
    }

    @SuppressWarnings("unchecked")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public JobItem createFromParcel(Parcel in)
        {
            return new JobItem(in);
        }

        public JobItem[] newArray(int size)
        {
            return new JobItem[size];
        }
    };




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

    public long get_lon()
    {
        return _lon;
    }

    public long get_lat()
    {
        return _lat;
    }

    public List get_pictures()
    {
        return _pictures;
    }

    public int get_price()
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
        _lon = l1;
    }

    public void set_pictures(List list)
    {
        _pictures = list;
    }

    public void set_price(int s)
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

}
