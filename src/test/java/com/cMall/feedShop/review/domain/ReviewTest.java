package com.cMall.feedShop.review.domain;

import com.cMall.feedShop.review.domain.enums.Cushion;
import com.cMall.feedShop.review.domain.enums.ReviewStatus;
import com.cMall.feedShop.review.domain.enums.SizeFit;
import com.cMall.feedShop.review.domain.enums.Stability;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Review 엔티티 테스트")
class ReviewTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testLogin", "password", "test@test.com", UserRole.USER);
    }

    @Test
    @DisplayName("유효한 데이터로 리뷰를 생성할 수 있다")
    void createReviewWithValidData() {
        // given & when
        Review review = Review.builder()
                .title("좋은 신발입니다")
                .rating(5)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.SOFT)
                .stability(Stability.STABLE)
                .content("정말 편하고 좋습니다. 추천해요!")
                .user(testUser)
                .productId(1L)
                .build();

        // then
        assertThat(review.getTitle()).isEqualTo("좋은 신발입니다");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getSizeFit()).isEqualTo(SizeFit.NORMAL);
        assertThat(review.getCushion()).isEqualTo(Cushion.SOFT);
        assertThat(review.getStability()).isEqualTo(Stability.STABLE);
        assertThat(review.getContent()).isEqualTo("정말 편하고 좋습니다. 추천해요!");
        assertThat(review.getUser()).isEqualTo(testUser);
        assertThat(review.getProductId()).isEqualTo(1L);
        assertThat(review.getPoints()).isEqualTo(0);
        assertThat(review.getStatus()).isEqualTo(ReviewStatus.ACTIVE);
        assertThat(review.getIsBlinded()).isFalse();
    }

    @Test
    @DisplayName("포인트를 추가할 수 있다")
    void addPoint() {
        // given
        Review review = Review.builder()
                .title("테스트")
                .rating(5)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.MEDIUM)
                .stability(Stability.NORMAL)
                .content("테스트 내용입니다")
                .user(testUser)
                .productId(1L)
                .build();

        // when
        review.addPoint();
        review.addPoint();

        // then
        assertThat(review.getPoints()).isEqualTo(2);
    }

    @Test
    @DisplayName("포인트를 제거할 수 있다")
    void removePoint() {
        // given
        Review review = Review.builder()
                .title("테스트")
                .rating(5)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.MEDIUM)
                .stability(Stability.NORMAL)
                .content("테스트 내용입니다")
                .user(testUser)
                .productId(1L)
                .build();
        review.addPoint();
        review.addPoint();
        review.addPoint();

        // when
        review.removePoint();

        // then
        assertThat(review.getPoints()).isEqualTo(2);
    }

    @Test
    @DisplayName("포인트가 0일 때 제거하면 0으로 유지된다")
    void removePointWhenZero() {
        // given
        Review review = Review.builder()
                .title("테스트")
                .rating(5)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.MEDIUM)
                .stability(Stability.NORMAL)
                .content("테스트 내용입니다")
                .user(testUser)
                .productId(1L)
                .build();

        // when
        review.removePoint();

        // then
        assertThat(review.getPoints()).isEqualTo(0);
    }

    @Test
    @DisplayName("활성 상태인 리뷰를 확인할 수 있다")
    void isActiveReview() {
        // given
        Review review = Review.builder()
                .title("테스트")
                .rating(5)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.MEDIUM)
                .stability(Stability.NORMAL)
                .content("테스트 내용입니다")
                .user(testUser)
                .productId(1L)
                .build();

        // when & then
        assertThat(review.isActive()).isTrue();
    }

    @Test
    @DisplayName("빈 제목으로 리뷰를 생성하면 예외가 발생한다")
    void createReviewWithEmptyTitle() {
        // when & then
        assertThatThrownBy(() -> Review.builder()
                .title("")
                .rating(5)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.MEDIUM)
                .stability(Stability.NORMAL)
                .content("테스트 내용입니다")
                .user(testUser)
                .productId(1L)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리뷰 제목은 필수입니다.");
    }

    @Test
    @DisplayName("잘못된 평점으로 리뷰를 생성하면 예외가 발생한다")
    void createReviewWithInvalidRating() {
        // when & then
        assertThatThrownBy(() -> Review.builder()
                .title("테스트 제목")
                .rating(6)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.MEDIUM)
                .stability(Stability.NORMAL)
                .content("테스트 내용입니다")
                .user(testUser)
                .productId(1L)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("평점은 1점에서 5점 사이여야 합니다.");
    }

    @Test
    @DisplayName("빈 내용으로 리뷰를 생성하면 예외가 발생한다")
    void createReviewWithEmptyContent() {
        // when & then
        assertThatThrownBy(() -> Review.builder()
                .title("테스트 제목")
                .rating(5)
                .sizeFit(SizeFit.NORMAL)
                .cushion(Cushion.MEDIUM)
                .stability(Stability.NORMAL)
                .content("")
                .user(testUser)
                .productId(1L)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리뷰 내용은 필수입니다.");
    }
}