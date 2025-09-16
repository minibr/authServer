package com.rest1.domain.post.post.controller;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.repository.MemberRepository;
import com.rest1.domain.post.post.entity.Post;
import com.rest1.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("글 다건 조회")
    void t1() throws Exception {

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk());

        resultActions
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].id", containsInRelativeOrder(3, 1)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].createDate").exists())
                .andExpect(jsonPath("$[0].modifyDate").exists())
                .andExpect(jsonPath("$[0].title").value("제목3"))
                .andExpect(jsonPath("$[0].content").value("내용3"))
                .andExpect(jsonPath("$[0].authorId").value(4))
                .andExpect(jsonPath("$[0].authorName").value("유저2"));


        // 하나 또는 2개 정도만 검증


//        for(int i = 0; i < posts.size(); i++) {
//            Post post = posts.get(i);
//
//            resultActions
//                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
//                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(matchesPattern(post.getCreateDate().toString().replaceAll("0+$", "") + ".*")))
//                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(matchesPattern(post.getModifyDate().toString().replaceAll("0+$", "") + ".*")))
//                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
//                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
//        }
    }

    @Test
    @DisplayName("글 단건 조회")
    void t2() throws Exception {

        long targetId = 1;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/%d".formatted(targetId))
                )
                .andDo(print());

        Post post = postRepository.findById(targetId).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.createDate").value(matchesPattern(post.getCreateDate().toString().replaceAll("0+$", "") + ".*")))
                .andExpect(jsonPath("$.modifyDate").value(matchesPattern(post.getModifyDate().toString().replaceAll("0+$", "") + ".*")))
                .andExpect(jsonPath("$.title").value("제목1"))
                .andExpect(jsonPath("$.content").value("내용1"))
                .andExpect(jsonPath("$.authorId").value(3))
                .andExpect(jsonPath("$.authorName").value("유저1"));
    }

    @Test
    @DisplayName("글 생성")
    void t3() throws Exception {
        String title = "제목입니다";
        String content = "내용입니다";
        Member author = memberRepository.findByUsername("user1").get();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("4번 게시물이 생성되었습니다."))
                .andExpect(jsonPath("$.data.postDto.id").value(4))
                .andExpect(jsonPath("$.data.postDto.createDate").exists())
                .andExpect(jsonPath("$.data.postDto.modifyDate").exists())
                .andExpect(jsonPath("$.data.postDto.title").value(title))
                .andExpect(jsonPath("$.data.postDto.content").value(content))
                .andExpect(jsonPath("$.data.postDto.authorId").value(author.getId()))
                .andExpect(jsonPath("$.data.postDto.authorName").value(author.getName()));

    }


    @Test
    @DisplayName("글 수정")
    void t4() throws Exception {
        long targetId = 1;
        String title = "제목 수정";
        String content = "내용 수정";

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/%d".formatted(targetId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());

        // 필수 검증
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modifyItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 게시물이 수정되었습니다.".formatted(targetId)));

        // 선택적 검증
        Post post = postRepository.findById(targetId).get();

        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("글 삭제")
    void t5() throws Exception {
        long targetId = 1;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/%d".formatted(targetId))
                )
                .andDo(print());

        // 필수 검증
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("deleteItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 게시물이 삭제되었습니다.".formatted(targetId)));

        // 선택적 검증
        Post post = postRepository.findById(targetId).orElse(null);
        assertThat(post).isNull();
    }

    @Test
    @DisplayName("글 단건 조회, 존재하지 않는 글")
    void t6() throws Exception {
        long targetId = Integer.MAX_VALUE;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/%d".formatted(targetId))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("글 작성, 제목이 입력되지 않은 경우")
    void t7() throws Exception {
        String title = "";
        String content = "내용입니다";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        title-NotBlank-must not be blank
                        title-Size-size must be between 2 and 10
                        """.stripIndent().trim()));


    }

    @Test
    @DisplayName("글 작성, 내용이 입력되지 않은 경우")
    void t8() throws Exception {
        String title = "제목입니다.";
        String content = "";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        content-NotBlank-must not be blank
                        content-Size-size must be between 2 and 100
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("글 작성, JSON 양식이 잘못된 경우")
    void t9() throws Exception {
        String title = "제목입니다.";
        String content = "내용입니다";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s"
                                            "content": "%s"
                                        
                                        """.formatted(title, content))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-2"))
                .andExpect(jsonPath("$.msg").value("잘못된 형식의 요청 데이터입니다."));
    }
}
