package com.insight.base.tenant.common.entity;

import com.insight.utils.pojo.BaseXo;

/**
 * @author 宣炳刚
 * @date 2019/05/20
 * @remark 租户企业信息实体类
 */
public class CompanyInfo extends BaseXo {

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 企业LOGO
     */
    private String logo;

    /**
     * 企业官网
     */
    private String homeSit;

    /**
     * 营业执照号码
     */
    private String license;

    /**
     * 营业执照照片URL
     */
    private String licenseImage;

    /**
     * 公司所在省/直辖市ID
     */
    private String provinceId;

    /**
     * 公司所在省/直辖市
     */
    private String province;

    /**
     * 公司所在市ID
     */
    private String cityId;

    /**
     * 公司所在市
     */
    private String city;

    /**
     * 公司所在区县ID
     */
    private String countyId;

    /**
     * 公司所在区县
     */
    private String county;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactMailbox;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getHomeSit() {
        return homeSit;
    }

    public void setHomeSit(String homeSit) {
        this.homeSit = homeSit;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseImage() {
        return licenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        this.licenseImage = licenseImage;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactMailbox() {
        return contactMailbox;
    }

    public void setContactMailbox(String contactMailbox) {
        this.contactMailbox = contactMailbox;
    }
}
