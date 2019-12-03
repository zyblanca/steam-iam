package com.crc.crcloud.steam.iam.model.feign.user;


import java.util.Date;
import java.util.Optional;

/**
 * @author tankang3
 * @Date 2019-06-25
 * @Desc 用户
 */
public class UserDTO {

    private Long    id;
    private String  loginName;
    private String  email;
    private Long    organizationId;
    private String  password;
    private String  realName;
    private String  phone;
    private String  imageUrl;
    private String  profilePhoto;
    private Boolean isEnabled;
    private Boolean isLdap;
    private Boolean enabled;
    private Boolean ldap;
    private String  language;
    private String  timeZone;
    private Date    lastPasswordUpdatedAt;
    private Date    lastLoginAt;
    private Boolean isLocked; //连续登录错误次数超出规定次数后是否锁定账户
    private Date    lockedUntilAt;
    private Integer passwordAttempt;
    private String internationalTelCode;
    private Boolean admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Boolean getLdap() {
        return Optional.ofNullable(isLdap).orElse(ldap);
    }

    public void setLdap(Boolean ldap) {
        isLdap = ldap;
        this.ldap = ldap;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Date getLastPasswordUpdatedAt() {
        return lastPasswordUpdatedAt;
    }

    public void setLastPasswordUpdatedAt(Date lastPasswordUpdatedAt) {
        this.lastPasswordUpdatedAt = lastPasswordUpdatedAt;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Date getLockedUntilAt() {
        return lockedUntilAt;
    }

    public void setLockedUntilAt(Date lockedUntilAt) {
        this.lockedUntilAt = lockedUntilAt;
    }

    public Integer getPasswordAttempt() {
        return passwordAttempt;
    }

    public void setPasswordAttempt(Integer passwordAttempt) {
        this.passwordAttempt = passwordAttempt;
    }

    public Boolean getEnabled() {
        return Optional.ofNullable(isEnabled).orElse(enabled);
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
        this.enabled = enabled;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getInternationalTelCode() {
        return internationalTelCode;
    }

    public void setInternationalTelCode(String internationalTelCode) {
        this.internationalTelCode = internationalTelCode;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
