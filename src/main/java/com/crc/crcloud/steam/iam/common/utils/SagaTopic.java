package com.crc.crcloud.steam.iam.common.utils;

public final class SagaTopic {
    private SagaTopic() {
    }

    public static class User {
        private User() {
        }

        // 创建用户
        public static final String USER_CREATE = "steam-iam-create-user";
        // iam 接受创建组织事件的 SagaTaskCode
        public static final String TASK_USER_CREATE = "steam-task-create-user";
        // 批量创建用户
        public static final String USER_CREATE_BATCH = "steam-iam-create-user";
        //更新用户
        public static final String USER_UPDATE = "steam-iam-update-user";
        //删除用户
        public static final String USER_DELETE = "steam-iam-delete-user";
        //启用用户
        public static final String USER_ENABLE = "steam-iam-enable-user";
        //停用用户
        public static final String USER_DISABLE = "steam-iam-disable-user";
    }

    public static class Project {
        private Project() {
        }

        // 创建项目
        public static final String PROJECT_CREATE = "steam-iam-create-project";
        // 更新项目
        public static final String PROJECT_UPDATE = "steam-iam-update-project";
        // 停用项目
        public static final String PROJECT_DISABLE = "steam-iam-disable-project";
        // 启用项目
        public static final String PROJECT_ENABLE = "steam-iam-enable-project";

    }

    public static class MemberRole {
        private MemberRole() {
        }

        //更新用户角色
        public static final String MEMBER_ROLE_UPDATE = "steam-iam-update-memberRole";
        //删除用户角色
        public static final String MEMBER_ROLE_DELETE = "steam-iam-delete-memberRole";
    }

    public static class Organization {
        private Organization() {
        }

        // 组织服务创建组织
        public static final String ORG_CREATE = "steam-org-create-organization";
        // 组织服务注册组织
        public static final String ORG_REGISTER = "steam-register-org";
        // iam 接收创建组织事件的 SagaTaskCode
        public static final String TASK_ORG_CREATE = "steam-iam-create-organization";
        // iam 接受注册组织:创建默认密码策略，创建默认 ldap 配置
        public static final String TASK_ORG_REGISTER_INIT_ORG = "steam-register-iam-init-org";
        // iam 接受注册组织:创建项目
        public static final String TASK_ORG_REGISTER_INIT_PROJ = "steam-register-iam-init-project";
        // 启用组织
        public static final String ORG_ENABLE = "steam-iam-enable-organization";
        // 停用组织
        public static final String ORG_DISABLE = "steam-iam-disable-organization";
        // 更新组织
        public static final String ORG_UPDATE = "steam-iam-update-organization";
    }

    public static class Application {
        private Application() {
        }

        // 创建应用
        public static final String APP_CREATE = "steam-iam-create-application";
        // 更新应用
        public static final String APP_UPDATE = "steam-iam-update-application";
        // 禁用应用
        public static final String APP_DISABLE = "steam-iam-disable-application";
        // 启用应用
        public static final String APP_ENABLE = "steam-iam-enable-application";

        /**
         * SagaTask
         */

        public static final String APP_SYNC = "devops-sync-application";
        public static final String IAM_SYNC_APP = "iam-sync-application";

    }

}
