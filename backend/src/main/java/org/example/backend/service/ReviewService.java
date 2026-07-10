package org.example.backend.service;

import org.example.backend.exeption.*;
import org.example.backend.model.Advertisement;
import org.example.backend.model.Review;
import org.example.backend.model.ReviewDTO;
import org.example.backend.model.User;
import org.example.backend.repository.AdvertisementRepository;
import org.example.backend.repository.ConversationRepository;
import org.example.backend.repository.ReviewRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReviewService {
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ReviewRepository reviewRepository;
    private final ConversationRepository conversationRepository;
    public ReviewService(UserRepository userRepository , AdvertisementRepository advertisementRepository ,
                         ReviewRepository reviewRepository , ConversationRepository conversationRepository) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
        this.reviewRepository = reviewRepository;
        this.conversationRepository= conversationRepository;
    }


    public synchronized boolean submitReview(ReviewDTO review, String loggedUserId , String advertisementId) {
        if ( review.getScore() > 5 || review.getScore() < 1 ){
            throw new InvalidScoreException("اﻣﺘﯿﺎز ﺑﺎﯾﺪ ﺑﯿﻦ ۱ ﺗﺎ ۵ ﺑﺎﺷﺪ.");
        }
        User user = userRepository.findByID(loggedUserId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if ( !user.isEnabled()){
            throw new UserBannedException("حساب کاربری شما مسدود است و اجازه ثبت نظر ندارید");
        }
        Advertisement advertisement = advertisementRepository.findByID(advertisementId).orElseThrow(() -> new AdvertisementNotFoundException("اگهی یافت نشد"));
        User seller = userRepository.findByID(advertisement.getOwnerId()).orElseThrow(() -> new UserNotFoundException("صاحب اگهی یافت نشد"));

        if (loggedUserId.equals(seller.getId())){
            throw new NoAcceessException("شما نمیتوانید به خودتان امتیاز بدهید");
        }
        String reviewId = loggedUserId + "_" + advertisementId;
        if (reviewRepository.existsById(reviewId)) {
            throw new ReviewAlreadyExistsException("شما قبلاً امتیاز خود را برای این آگهی ثبت کرده‌اید");
        }

        String expectedConversationId = loggedUserId +"_"+advertisementId;
        if ( !conversationRepository.existsById(expectedConversationId)){
            throw new NoAcceessException("شما تنها در صورتی می‌توانید امتیاز دهید که درباره این آگهی با فروشنده گفت‌وگو کرده باشید.");
        }
        Review review1 = new Review();
        review1.setId(reviewId);
        review1.setReviewerId(loggedUserId);
        review1.setScore(review.getScore());
        review1.setSellerId(seller.getId());
        review1.setAdvertisementId(advertisementId);
        review1.setComment(review.getComment() != null ? review.getComment().trim() : "");

        int currentCount = seller.getTotalRatingsCount();
        double currentAvg = seller.getAverageRating();

        double newAvg = ((currentCount * currentAvg) + review.getScore()) / (currentCount + 1);
        newAvg = Math.round(newAvg * 100.0) / 100.0;
        seller.setTotalRatingsCount(currentCount + 1);
        seller.setAverageRating(newAvg);

        userRepository.save(seller);
        reviewRepository.save(review1);

        return true;
    }

    public List<Review> getReviewsForUser(String targetId , String userId) {
        User user = userRepository.findByID(userId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if (!user.isEnabled()){
            throw new UserBannedException("شما مسدود هستید");
        }
        userRepository.findByID(targetId)
                .orElseThrow(() -> new UserNotFoundException("فروشنده مورد نظر یافت نشد"));

        return reviewRepository.findBySellerId(targetId);
    }

}