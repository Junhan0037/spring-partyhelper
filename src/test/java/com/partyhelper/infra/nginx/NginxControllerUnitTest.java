package com.partyhelper.infra.nginx;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class NginxControllerUnitTest {

    @Test
    public void real_profile_return() {
        //given
        String exptectedProfile = "real";
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile(exptectedProfile);
        env.addActiveProfile("oauth");
        env.addActiveProfile("real-db");

        NginxController controller = new NginxController(env);

        //when
        String profile = controller.nginx();

        //then
        assertThat(profile).isEqualTo(exptectedProfile);
    }

    @Test
    public void real_profile_first_return() {
        //given
        String expectedProfile = "oauth";
        MockEnvironment env = new MockEnvironment();

        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("real-db");

        NginxController controller = new NginxController(env);

        //when
        String profile = controller.nginx();

        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    public void real_profile_default_return() {
        //given
        String exptectedProfile = "default";
        MockEnvironment env = new MockEnvironment();
        NginxController controller = new NginxController(env);

        //when
        String profile = controller.nginx();

        //then
        assertThat(profile).isEqualTo(exptectedProfile);
    }


}