package com.sparta.baemineats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.baemineats.config.WebSecurityConfig;
import com.sparta.baemineats.dto.requestDto.LoginRequestDto;
import com.sparta.baemineats.dto.requestDto.MenuRequest;
import com.sparta.baemineats.dto.requestDto.StoreRequest;
import com.sparta.baemineats.entity.Store;
import com.sparta.baemineats.entity.User;
import com.sparta.baemineats.entity.UserRoleEnum;
import com.sparta.baemineats.repository.StoreRepository;
import com.sparta.baemineats.security.UserDetailsImpl;
import com.sparta.baemineats.service.MenuService;
import com.sparta.baemineats.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(
        controllers = {UserController.class, MenuController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)

@DisabledInAotMode
class MenuControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    MenuService menuService;

    @MockBean
    StoreRepository storeRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {

        String username = "asdf1234";
        String password = "asdf1234A!";
        String address = "경기도";
        String email = "asd@email.com";
        UserRoleEnum role = UserRoleEnum.SELLER;
        User testUser = User.builder()
                .id(1L)
                .username(username)
                .password(password)
                .address(address)
                .email(email)
                .role(role)
                .build();
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("새로운 메뉴 등록")
    void test3() throws Exception {
        // given
        this.mockUserSetup();
        String storeName = "홍콩반점";
        String storeDescription = "맛있는 가게";
        StoreRequest request = new StoreRequest(storeName,storeDescription);
        Store store = storeRepository.save(new Store(request, "사장"));


        String menuName = "탕수육";
        int menuPrice = 25000;
        String menuDescription = "돼지고기 : 국내산";

        MockMultipartFile image = new MockMultipartFile(
                "test",
                "a.png",
                "image/png",
                new FileInputStream(new File("C:/Users/Owner/Pictures/Screenshots/a.png")));

        MenuRequest requestDto = new MenuRequest(menuName, menuPrice, menuDescription, image);
        Long storeId = 1L;
        // when - then
        mvc.perform(
                multipart("/api/menus/stores/{storeId}",1)
                        .file(image)
                        .param("menuName", "탕수육")
                        .param("menuPrice", "25000")
                        .param("menuDescription", "돼지고기 : 국내산")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


}
