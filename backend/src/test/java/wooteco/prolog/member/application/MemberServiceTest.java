package wooteco.prolog.member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import wooteco.prolog.login.application.dto.GithubProfileResponse;
import wooteco.prolog.login.ui.LoginMember;
import wooteco.prolog.member.application.dto.MemberResponse;
import wooteco.prolog.member.application.dto.MemberUpdateRequest;
import wooteco.prolog.member.application.dto.MembersResponse;
import wooteco.prolog.member.application.dto.ProfileIntroRequest;
import wooteco.prolog.member.application.dto.ProfileIntroResponse;
import wooteco.prolog.member.domain.Member;
import wooteco.prolog.member.domain.Role;
import wooteco.prolog.member.domain.repository.MemberRepository;
import wooteco.prolog.member.exception.MemberNotAllowedException;
import wooteco.prolog.member.exception.MemberNotFoundException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    @DisplayName("findOrCreateMember() : gitHub Id 를 통해서 이미 존재한 Member 조회")
    void findOrCreateMember_find() {
        //given
        final Member member = new Member(1L, "a", "a", Role.ADMIN, 1L, "imageUrl");

        final GithubProfileResponse githubProfileResponse =
            new GithubProfileResponse("a",
                "a",
                "1",
                "imageUrl"
            );

        //when
        when(memberRepository.findByGithubId(any()))
            .thenReturn(Optional.of(member));

        final Member foundMember = memberService.findOrCreateMember(githubProfileResponse);

        //then
        assertEquals(foundMember, member);
    }

    @Test
    @DisplayName("findOrCreateMember() : 존재하지 않는 회원일 경우, GitHub Id를 통해서 새로 생성한다.")
    void findOrCreateMember_create() {
        //given
        final Member member = new Member(1L, "a", "a", Role.ADMIN, 1L, "imageUrl");

        final GithubProfileResponse githubProfileResponse =
            new GithubProfileResponse("a",
                "a",
                "1",
                "imageUrl"
            );

        //when
        when(memberRepository.findByGithubId(any()))
            .thenReturn(Optional.empty());

        when(memberRepository.save(any()))
            .thenReturn(member);

        final Member savedMember = memberService.findOrCreateMember(githubProfileResponse);

        //then
        assertEquals(savedMember, member);
    }

    @Test
    @DisplayName("findById() : Id를 통해서 Member를 조회할 수 있다.")
    void findById() {
        //given
        final Member member = new Member(1L, "a", "a", Role.ADMIN, 1L, "imageUrl");

        //when
        when(memberRepository.findById(any()))
            .thenReturn(Optional.of(member));

        final Member foundMember = memberService.findById(1L);

        //then
        assertEquals(foundMember, member);
    }

    @Test
    @DisplayName("findById() : 존재하지 않는 Member를 조회할 경우 MemberNotFoundException이 발생한다.")
    void findById_MemberNotFoundException() {
        //when
        when(memberRepository.findById(any()))
            .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.findById(1L))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("findByUsername() : username을 통해서 Member를 조회할 수 있다.")
    void findByUsername() {
        //given
        final Member member = new Member(1L, "a", "a", Role.ADMIN, 1L, "imageUrl");

        //when
        when(memberRepository.findByUsername(any()))
            .thenReturn(Optional.of(member));

        final Member foundMember = memberService.findByUsername("a");

        //then
        assertEquals(foundMember, member);
    }

    @Test
    @DisplayName("findByUsername() : 존재하지 않는 username을 통해서 Member를 조회할 경우 MemberNotFoundException이 발생한다.")
    void findByUsername_MemberNotFoundException() {
        //when
        when(memberRepository.findByUsername(any()))
            .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.findByUsername("a"))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("findMemberResponseByUsername() : username을 통해서 MemberResponse를 조회할 수 있다.")
    void findMemberResponseByUsername() {
        //given
        final MemberResponse memberResponse = new MemberResponse(1L, "a", "a", Role.ADMIN,
            "imageUrl");
        final Member member = new Member(1L, "a", "a", Role.ADMIN, 1L, "imageUrl");

        //when
        when(memberRepository.findByUsername(any()))
            .thenReturn(Optional.of(member));

        final MemberResponse foundMemberResponse = memberService.findMemberResponseByUsername("a");

        //then
        assertEquals(foundMemberResponse, memberResponse);
    }

    @Test
    @DisplayName("ProfileIntroResponse() : username을 통해서 ProfileIntroResponse를 조회할 수 있다.")
    void profileIntroResponse() {
        //given
        final ProfileIntroResponse profileIntroResponse = new ProfileIntroResponse("hi");
        final Member member = new Member(1L, "username", "a", Role.ADMIN, 1L, "imageUrl");
        member.updateProfileIntro("hi");

        //when
        when(memberRepository.findByUsername(any()))
            .thenReturn(Optional.of(member));

        final ProfileIntroResponse savedProfileIntroResponse = memberService.findProfileIntro(member.getUsername());

        //then
        assertEquals(savedProfileIntroResponse.getText(), profileIntroResponse.getText());
    }

    @Test
    @DisplayName("updateMember() : ANONYMOUS인 회원일 경우 update 할 때, MemberNotAllowedException이 발생한다.")
    void updateMember_MemberNotAllowedException_anonymous() {
        //given
        final LoginMember loginMember = new LoginMember(LoginMember.Authority.ANONYMOUS);
        final MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("a", "imageUrl");

        //when & then
        assertThatThrownBy(() -> memberService.updateMember(loginMember, "a", memberUpdateRequest))
            .isInstanceOf(MemberNotAllowedException.class);
    }

    @Test
    @DisplayName("updateMember() : 로그인 된 Member와 수정할 Member의 username이 다르면 MemberNotAllowedException이 발생한다.")
    void updateMember_MemberNotAllowedException_different_name() {
        //given
        final LoginMember loginMember = new LoginMember(LoginMember.Authority.MEMBER);
        final Member member = new Member(1L, "a", "a", Role.CREW, 1L, "imageUrl");
        final MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("a", "imageUrl");

        //when
        when(memberRepository.findById(any()))
            .thenReturn(Optional.of(member));

        //then
        assertThatThrownBy(() -> memberService.updateMember(loginMember, "b", memberUpdateRequest))
            .isInstanceOf(MemberNotAllowedException.class);
    }

    @Test
    @DisplayName("updateMember() : 로그인 된 Member와 수정할 Member의 username이 같으면 imageUrl과 nickname을 업데이트 할 수 있다.")
    void updateMember() {
        //given
        final LoginMember loginMember = new LoginMember(LoginMember.Authority.MEMBER);
        final Member member = new Member(1L, "a", "a", Role.CREW, 1L, "imageUrl");
        final MemberUpdateRequest memberUpdateRequest =
            new MemberUpdateRequest("b", "updateImageUrl");

        //when
        when(memberRepository.findById(any()))
            .thenReturn(Optional.of(member));

        memberService.updateMember(loginMember, "a", memberUpdateRequest);

        //then
        assertAll(
            () -> assertEquals("b", member.getNickname()),
            () -> assertEquals("updateImageUrl", member.getImageUrl())
        );
    }

    @Test
    @DisplayName("updateProfileIntro() : ANONYMOUS인 회원일 경우 profile을 update 할 때, MemberNotAllowedException이 발생한다.")
    void updateProfileIntro_MemberNotAllowedException_anonymous() {
        //given
        final LoginMember loginMember = new LoginMember(LoginMember.Authority.ANONYMOUS);
        final ProfileIntroRequest profileIntroRequest = new ProfileIntroRequest("text");

        //when & then
        assertThatThrownBy(
            () -> memberService.updateProfileIntro(loginMember, "a", profileIntroRequest))
            .isInstanceOf(MemberNotAllowedException.class);
    }

    @Test
    @DisplayName("updateProfileIntro() : 로그인 된 Member와 수정할 Member의 username이 다르면 MemberNotAllowedException이 발생한다.")
    void updateProfileIntro_MemberNotAllowedException_different_name() {
        //given
        final LoginMember loginMember = new LoginMember(LoginMember.Authority.MEMBER);
        final Member member = new Member(1L, "a", "a", Role.CREW, 1L, "imageUrl");
        final ProfileIntroRequest profileIntroRequest = new ProfileIntroRequest("text");

        //when
        when(memberRepository.findById(any()))
            .thenReturn(Optional.of(member));

        //then
        assertThatThrownBy(
            () -> memberService.updateProfileIntro(loginMember, "b", profileIntroRequest))
            .isInstanceOf(MemberNotAllowedException.class);
    }

    @Test
    @DisplayName("updateProfileIntro() : 로그인 된 Member와 수정할 Member의 username이 같으면 profileIntro를 업데이트 할 수 있다.")
    void updateProfileIntro() {
        //given
        final LoginMember loginMember = new LoginMember(LoginMember.Authority.MEMBER);
        final Member member = new Member(1L, "a", "a", Role.CREW, 1L, "imageUrl");
        final ProfileIntroRequest profileIntroRequest = new ProfileIntroRequest("text");

        //when
        when(memberRepository.findById(any()))
            .thenReturn(Optional.of(member));

        memberService.updateProfileIntro(loginMember, "a", profileIntroRequest);

        //then
        assertEquals(member.getProfileIntro(), profileIntroRequest.getText());
    }

    @Test
    @DisplayName("findAllOrderByNickNameAsc() : nickname을 통해서 모든 회원을 조회하고, 닉네임 순으로 오름차순 정렬할 수 있다.")
    void findAllOrderByNickNameAsc() {
        //given
        final List<Member> members = Arrays.asList(
            new Member("a", "d", Role.CREW, 1L, "imageUrl1"),
            new Member("b", "b", Role.CREW, 2L, "imageUrl2"),
            new Member("c", "c", Role.CREW, 3L, "imageUrl3"),
            new Member("d", "a", Role.CREW, 4L, "imageUrl4")
        );

        when(memberRepository.findAll())
            .thenReturn(members);

        //when
        final List<MemberResponse> savedMembers = memberService.findAllOrderByNickNameAsc();

        //then
        assertThat(savedMembers).isSortedAccordingTo(
            Comparator.comparing(MemberResponse::getNickname));
    }

    @Test
    @DisplayName("findAll(): Pageable을 통해 MembersResponse를 조회할 수 있다.")
    void findAll() {
        //given
        final List<Member> members = LongStream.range(1, 9).boxed()
            .map(id -> new Member(id, id.toString(), id.toString(), Role.CREW, id, "imageUrl" + id))
            .collect(Collectors.toList());
        final PageRequest pageRequest = PageRequest.of(1, 2);
        final Page<Member> pages = new PageImpl<>(members, pageRequest, members.size());

        when(memberRepository.findAll(pageRequest))
            .thenReturn(pages);

        //when
        final MembersResponse membersResponse = memberService.findAll(pageRequest);

        //then
        assertAll(
            () -> assertEquals(2, membersResponse.getCurrPage()),
            () -> assertEquals(4, membersResponse.getTotalPage()),
            () -> assertEquals(8L, membersResponse.getTotalSize()),
            () -> assertEquals(8, membersResponse.getData().size())
        );
    }

    @Test
    @DisplayName("findByIdIn(): 여러개의 memberId를 통해서 Member들을 조회할 수 있다.")
    void findByIdIn() {
        //given
        final List<Member> members = Arrays.asList(
            new Member(1L, "a", "d", Role.CREW, 1L, "imageUrl1"),
            new Member(2L, "b", "b", Role.CREW, 2L, "imageUrl2"),
            new Member(3L, "c", "c", Role.CREW, 3L, "imageUrl3"),
            new Member(4L, "d", "a", Role.CREW, 4L, "imageUrl4")
        );

        final List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        when(memberRepository.findByIdIn(ids))
            .thenReturn(members);

        //when
        final List<Member> savedMembers = memberService.findByIdIn(ids);

        //then
        assertEquals(members.size(), savedMembers.size());
    }
}
