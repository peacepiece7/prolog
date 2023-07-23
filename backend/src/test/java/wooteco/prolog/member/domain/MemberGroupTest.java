package wooteco.prolog.member.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MemberGroupTest {

    private static final MemberGroup ANDROID_GROUP = new MemberGroup(null, " 안드로이드 5기", "A");
    private static final MemberGroup BACKEND_GROUP = new MemberGroup(null, " 백엔드 5기", "B");
    private static final MemberGroup FRONTEND_GROUP = new MemberGroup(null, " 프론트엔드 5기", "F");

    @Test
    void getGroupType_이름이_그룹명을_포함하면_그룹을_반환한다() {
        assertThat(ANDROID_GROUP.groupType()).isEqualTo(MemberGroupType.ANDROID);
        assertThat(BACKEND_GROUP.groupType()).isEqualTo(MemberGroupType.BACKEND);
        assertThat(FRONTEND_GROUP.groupType()).isEqualTo(MemberGroupType.FRONTEND);
    }

    @Test
    void getGroupType_이름이_포함하는_그룹명이_없으면_예외가_발생한다() {
        MemberGroup memberGroup = new MemberGroup(null, "테스트", "test");

        assertThatThrownBy(memberGroup::groupType)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("그룹이 포함되는 타입이 없습니다. id=null");
    }

}
