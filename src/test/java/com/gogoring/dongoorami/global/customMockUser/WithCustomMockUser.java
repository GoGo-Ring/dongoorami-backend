package com.gogoring.dongoorami.global.customMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

    String name() default "김뫄뫄";

    String profileImage() default "image.png";

    String provider() default "kakao";

    String providerId() default "alsjkghlaskdjgh";
}
