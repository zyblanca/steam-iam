package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.entity.IamUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * LDAP 批量创建用户信息
 */
@Getter
public class IamUserLdapBatchCreateEvent extends ApplicationEvent {

    private Long organization;

    private List<IamUser> iamUsers;

    public IamUserLdapBatchCreateEvent(Long organization, List<IamUser> iamUsers) {
        super(iamUsers);
        this.iamUsers = iamUsers;
        this.organization = organization;

    }

}
