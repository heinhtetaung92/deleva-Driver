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
    /*private Requester _requester;*/
    private String _secret_code;
    private String _status;
    private String _type;


    private String _requester_address;
    private String _requester_business_type;
    private String _requester_confirmed;
    private String _requester_email;
    private String _requester_hash;
    private String _requester_id;
    private String _requester_type;
    private long _requester_lan;
    private long _requester_lat;
    private String _requester_mobile_number;
    private String _requester_name;
    private List _requester_pictures;
    private String _requester_salt;
    private String _requester_v;

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
        /*dest.writeSerializable(_requester);*/
        dest.writeString(_secret_code);
        dest.writeString(_status);
        dest.writeString(_type);



        dest.writeString(_requester_address);
        dest.writeString(_requester_confirmed);
        dest.writeString(_requester_email);
        dest.writeString(_requester_hash);
        dest.writeString(_requester_id);
        dest.writeString(_requester_type);
        dest.writeLong(_requester_lan);
        dest.writeLong(_requester_lat);
        dest.writeString(_requester_mobile_number);
        dest.writeString(_requester_business_type);
        dest.writeList(_requester_pictures);
        dest.writeString(_requester_name);
        dest.writeString(_requester_salt);
        dest.writeString(_requester_v);

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
       /* this._requester = (Requester) in.readSerializable();*/
        this._secret_code = in.readString();
        this._status = in.readString();
        this._type = in.readString();

        this._requester_address = in.readString();
        this._requester_confirmed = in.readString();
        this._requester_email = in.readString();
        this._requester_hash = in.readString();
        this._requester_id = in.readString();
        this._requester_type = in.readString();
        this._requester_lan = in.readLong();
        this._requester_lat = in.readLong();
        this._requester_mobile_number = in.readString();
        this._requester_business_type = in.readString();
        in.readList(this._requester_pictures, List.class.getClassLoader());
        this._requester_name = in.readString();
        this._requester_salt = in.readString();
        this._requester_v = in.readString();
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

    /*public Requester get_requester()
    {
        return _requester;
    }*/

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

    public void set_requester_latAndlan(long l, long l1){
        _requester_lat = l;
        _requester_lan = l1;
    }

    public void set_pictures(List list)
    {
        _pictures = list;
    }

    public void set_price(int s)
    {
        _price = s;
    }

   /* public void set_requester(Requester requester)
    {
        _requester = requester;
    }*/

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


    public String get_requester_address() {
        return _requester_address;
    }

    public void set_requester_address(String _requester_address) {
        this._requester_address = _requester_address;
    }

    public String get_requester_business_type() {
        return _requester_business_type;
    }

    public void set_requester_business_type(String _requester_business_type) {
        this._requester_business_type = _requester_business_type;
    }

    public String get_requester_confirmed() {
        return _requester_confirmed;
    }

    public void set_requester_confirmed(String _requester_confirmed) {
        this._requester_confirmed = _requester_confirmed;
    }

    public String get_requester_email() {
        return _requester_email;
    }

    public void set_requester_email(String _requester_email) {
        this._requester_email = _requester_email;
    }

    public String get_requester_hash() {
        return _requester_hash;
    }

    public void set_requester_hash(String _requester_hash) {
        this._requester_hash = _requester_hash;
    }

    public String get_requester_id() {
        return _requester_id;
    }

    public void set_requester_id(String _requester_id) {
        this._requester_id = _requester_id;
    }

    public String get_requester_type() {
        return _requester_type;
    }

    public void set_requester_type(String _requester_type) {
        this._requester_type = _requester_type;
    }

    public long get_requester_lan() {
        return _requester_lan;
    }

    public void set_requester_lan(long _requester_lan) {
        this._requester_lan = _requester_lan;
    }

    public long get_requester_lat() {
        return _requester_lat;
    }

    public void set_requester_lat(long _requester_lat) {
        this._requester_lat = _requester_lat;
    }

    public String get_requester_mobile_number() {
        return _requester_mobile_number;
    }

    public void set_requester_mobile_number(String _requester_mobile_number) {
        this._requester_mobile_number = _requester_mobile_number;
    }

    public String get_requester_name() {
        return _requester_name;
    }

    public void set_requester_name(String _requester_name) {
        this._requester_name = _requester_name;
    }

    public List get_requester_pictures() {
        return _requester_pictures;
    }

    public void set_requester_pictures(List _requester_pictures) {
        this._requester_pictures = _requester_pictures;
    }

    public String get_requester_salt() {
        return _requester_salt;
    }

    public void set_requester_salt(String _requester_salt) {
        this._requester_salt = _requester_salt;
    }

    public String get_requester_v() {
        return _requester_v;
    }

    public void set_requester_v(String _requester_v) {
        this._requester_v = _requester_v;
    }
}
