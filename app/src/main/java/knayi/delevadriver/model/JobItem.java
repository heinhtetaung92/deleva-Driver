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


    private String _id;

    //for requester
    private String _requester_name;
    private String _requester_email;
    private String _requester_mobile_number;
    private String _requester_address;
    //private String _requester_business_type;
    private String _requester_business_address;
    //private List _requester_pictures;
    private String _requester_picture;


    private String _address;
    private String _receiver_name;
    private String _receiver_contact;
    private String _post_code;
    //private List _pickup_ll;
    private Double _pickup_lat;
    private Double _pickup_lon;
    private String _pickup_address;
    //private List _address_ll;
    private Double _address_lat;
    private Double _address_lon;
    private List _reports;
    private List _rejectMessage;
    private String _status;
    private String _picture;
    private String _price;
    private String _createAt;

    //NEW
    private String _reciever_credit_card_type;
    private String _reciever_credit_card_no;
    private String _weight;
    private String _sensitivity;
    private String _size;
    private String _duration;
    private String _pickuptime;
    private String isExpress;
    private String isRefrigerated;



    /*private String _address;
    private String _createAt;
    private String _id;
    private long _lon;
    private long _lat;
    private List _pictures;
    private int _price;
    *//*private Requester _requester;*//*
    private String _secret_code;
    private String _status;
    private String _type;
    private String _receiver_name;
    private String _receiver_contact;
    private String _post_code;



    private String _requester_address;
    private String _requester_business_address;
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
    private String _requester_v;*/

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
        dest.writeString(_requester_email);
        dest.writeString(_requester_mobile_number);
        dest.writeString(_requester_address);
        //dest.writeString(_requester_business_type);
        dest.writeString(_requester_business_address);
        //dest.writeList(_requester_pictures);
        dest.writeString(_requester_picture);
        //dest.writeString(_type);
        dest.writeString(_address);
        dest.writeString(_receiver_name);
        dest.writeString(_receiver_contact);
        dest.writeString(_post_code);
        //dest.writeList(_pickup_ll);
        dest.writeDouble(_pickup_lat);
        dest.writeDouble(_pickup_lon);
        //dest.writeList(_address_ll);
        dest.writeDouble(_address_lat);
        dest.writeDouble(_address_lon);
        dest.writeList(_reports);
        dest.writeList(_rejectMessage);
        dest.writeString(_status);
        dest.writeString(_picture);
        dest.writeString(_price);
        dest.writeString(_createAt);
        dest.writeString(_reciever_credit_card_type);
        dest.writeString(_reciever_credit_card_no);
        dest.writeString(_weight);
        dest.writeString(_sensitivity);
        dest.writeString(_size);
        dest.writeString(_duration);
        dest.writeString(_pickuptime);
        dest.writeString(_pickup_address);
        dest.writeString(_requester_name);
        dest.writeString(isExpress);
        dest.writeString(isRefrigerated);


    }
    public JobItem(Parcel in)
    {

        _requester_email = in.readString();
        _requester_mobile_number = in.readString();
        _requester_address = in.readString();
        //_requester_business_type = in.readString();
        _requester_business_address = in.readString();
        //in.readList(_requester_pictures, List.class.getClassLoader());
        _requester_picture = in.readString();
        //_type = in.readString();
        _address = in.readString();
        _receiver_name = in.readString();
        _receiver_contact = in.readString();
        _post_code = in.readString();
        //in.readList(_pickup_ll, List.class.getClassLoader());
        _pickup_lat = in.readDouble();
        _pickup_lon = in.readDouble();
        //in.readList(_address_ll, List.class.getClassLoader());
        _address_lat = in.readDouble();
        _address_lon = in.readDouble();
        in.readList(_reports, List.class.getClassLoader());
        in.readList(_rejectMessage, List.class.getClassLoader());
        _status = in.readString();
        _picture = in.readString();
        _price = in.readString();
        _createAt = in.readString();
        _reciever_credit_card_type = in.readString();
        _reciever_credit_card_no = in.readString();
        _weight = in.readString();
        _sensitivity = in.readString();
        _size = in.readString();
        _duration = in.readString();
        _pickuptime = in.readString();
        _pickup_address = in.readString();
        _requester_name = in.readString();
        isExpress = in.readString();
        isRefrigerated = in.readString();

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


    public String get_duration() {
        return _duration;
    }

    public void set_duration(String _duration) {
        this._duration = _duration;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_requester_email() {
        return _requester_email;
    }

    public void set_requester_email(String _requester_email) {
        this._requester_email = _requester_email;
    }

    public String get_requester_mobile_number() {
        return _requester_mobile_number;
    }

    public void set_requester_mobile_number(String _requester_mobile_number) {
        this._requester_mobile_number = _requester_mobile_number;
    }

    public String get_requester_address() {
        return _requester_address;
    }

    public void set_requester_address(String _requester_address) {
        this._requester_address = _requester_address;
    }

    /*public String get_requester_business_type() {
        return _requester_business_type;
    }

    public void set_requester_business_type(String _requester_business_type) {
        this._requester_business_type = _requester_business_type;
    }*/

    public String get_requester_business_address() {
        return _requester_business_address;
    }

    public void set_requester_business_address(String _requester_business_address) {
        this._requester_business_address = _requester_business_address;
    }

    /*public List get_requester_pictures() {
        return _requester_pictures;
    }

    public void set_requester_pictures(List _requester_pictures) {
        this._requester_pictures = _requester_pictures;
    }*/


    public String get_requester_pictures() {
        return _requester_picture;
    }

    public void set_requester_pictures(String _requester_pictures) {
        this._requester_picture = _requester_pictures;
    }

    /*public String get_type() {
        return _type;
    }*/

    /*public void set_type(String _type) {
        this._type = _type;
    }*/

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_receiver_name() {
        return _receiver_name;
    }

    public void set_receiver_name(String _receiver_name) {
        this._receiver_name = _receiver_name;
    }

    public String get_receiver_contact() {
        return _receiver_contact;
    }

    public void set_receiver_contact(String _receiver_contact) {
        this._receiver_contact = _receiver_contact;
    }

    public String get_post_code() {
        return _post_code;
    }

    public void set_post_code(String _post_code) {
        this._post_code = _post_code;
    }

    /*public List get_pickup_ll() {
        return _pickup_ll;
    }

    public void set_pickup_ll(List _pickup_ll) {
        this._pickup_ll = _pickup_ll;
    }

    public List get_address_ll() {
        return _address_ll;
    }

    public void set_address_ll(List _address_ll) {
        this._address_ll = _address_ll;
    }*/

    public String get_requester_picture() {
        return _requester_picture;
    }

    public void set_requester_picture(String _requester_picture) {
        this._requester_picture = _requester_picture;
    }

    public Double get_pickup_lat() {
        return _pickup_lat;
    }

    public void set_pickup_lat(Double _pickup_lat) {
        this._pickup_lat = _pickup_lat;
    }

    public Double get_pickup_lon() {
        return _pickup_lon;
    }

    public void set_pickup_lon(Double _pickup_lon) {
        this._pickup_lon = _pickup_lon;
    }

    public Double get_address_lat() {
        return _address_lat;
    }

    public void set_address_lat(Double _address_lat) {
        this._address_lat = _address_lat;
    }

    public Double get_address_lon() {
        return _address_lon;
    }

    public void set_address_lon(Double _address_lon) {
        this._address_lon = _address_lon;
    }

    public List get_reports() {
        return _reports;
    }

    public void set_reports(List _reports) {
        this._reports = _reports;
    }

    public List get_rejectMessage() {
        return _rejectMessage;
    }

    public void set_rejectMessage(List _rejectMessage) {
        this._rejectMessage = _rejectMessage;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public String get_pictures() {
        return _picture;
    }

    public void set_pictures(String _pictures) {
        this._picture = _pictures;
    }

    public String get_price() {
        return _price;
    }

    public void set_price(String _price) {
        this._price = _price;
    }

    public String get_createAt() {
        return _createAt;
    }

    public void set_createAt(String _createAt) {
        this._createAt = _createAt;
    }

    public String get_reciever_credit_card_type() {
        return _reciever_credit_card_type;
    }

    public void set_reciever_credit_card_type(String _reciever_credit_card_type) {
        this._reciever_credit_card_type = _reciever_credit_card_type;
    }

    public String get_reciever_credit_card_no() {
        return _reciever_credit_card_no;
    }

    public void set_reciever_credit_card_no(String _reciever_credit_card_no) {
        this._reciever_credit_card_no = _reciever_credit_card_no;
    }

    public String get_weight() {
        return _weight;
    }

    public void set_weight(String _weight) {
        this._weight = _weight;
    }

    public String get_sensitivity() {
        return _sensitivity;
    }

    public void set_sensitivity(String _sensitivity) {
        this._sensitivity = _sensitivity;
    }

    public String get_picture() {
        return _picture;
    }

    public void set_picture(String _picture) {
        this._picture = _picture;
    }

    public String get_size() {
        return _size;
    }

    public void set_size(String _size) {
        this._size = _size;
    }

    public String get_pickuptime() {
        return _pickuptime;
    }

    public void set_pickuptime(String _pickuptime) {
        this._pickuptime = _pickuptime;
    }


    public String get_pickup_address() {
        return _pickup_address;
    }

    public void set_pickup_address(String _pickup_address) {
        this._pickup_address = _pickup_address;
    }

    public String get_requester_name() {
        return _requester_name;
    }

    public void set_requester_name(String _requester_name) {
        this._requester_name = _requester_name;
    }

    public String getIsExpress() {
        return isExpress;
    }

    public void setIsExpress(String isExpress) {
        this.isExpress = isExpress;
    }

    public String getIsRefrigerated() {
        return isRefrigerated;
    }

    public void setIsRefrigerated(String isRefrigerated) {
        this.isRefrigerated = isRefrigerated;
    }
}
