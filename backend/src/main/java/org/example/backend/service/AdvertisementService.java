package org.example.backend.service;

import org.example.backend.exeption.*;
import org.example.backend.model.*;
import org.example.backend.repository.AdvertisementRepository;
import org.example.backend.repository.AttributeRulesRepository;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AttributeRulesRepository attributeRulesRepository;

    public AdvertisementService(AdvertisementRepository advertisementRepository , UserRepository userRepository,
                                CategoryRepository categoryRepository , AttributeRulesRepository attributeRulesRepository) {
        this.advertisementRepository = advertisementRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.attributeRulesRepository = attributeRulesRepository;
    }

    public Advertisement createNewAdvertisement(Advertisement ad, List<String> imageUrls, String userID) {
        System.out.println("--> Received categoryId from frontend: '" + ad.getCategoryId() + "'");
        System.out.println("--> Categories available in backend cache: " + categoryRepository.findAll().stream().map(Category::getId).toList());
        User user = userRepository.findByID(userID).orElseThrow(() -> new UserNotFoundException("کاربر وجود ندارد"));
        if ( !user.isEnabled()){
            throw new UserBannedException("کاربر مسدود است");
        }
        if ( ad.getPrice() < 0 ){
            throw new PriceNegativeException("قیمت نامعتبر است");
        }
        if (ad.getTitle() == null ||  ad.getTitle().trim().isEmpty()){
            throw new TitleInvalidException("عنوان آگهی اجباری است");
        }
        Category category = categoryRepository.findByID(ad.getCategoryId()).orElseThrow(() -> new InvalidCategoryIdException("دسته بندی وجود ندارد"));
        if ( ad.getSpecificAttributes() == null){
            ad.setSpecificAttributes(new HashMap<>());
        }
        List<AttributeRule> rules = attributeRulesRepository.findByCategoryId(ad.getCategoryId());
        for ( AttributeRule rule : rules){
            if ( rule.isRequired() && !ad.getSpecificAttributes().containsKey(rule.getAttributeName())){
                throw new IllegalArgumentException("فیلد ضروری '" + rule.getAttributeName() + "' برای این دسته بندی وارد نشده است.");
            }
        }
        ad.setStatus(AdStatus.PENDING_REVIEW);
        ad.setImageUrls(imageUrls);
        ad.setCreatedAt(LocalDateTime.now());
        ad.setId(UUID.randomUUID().toString());
        ad.setOwnerId(userID);
        advertisementRepository.save(ad);

        return ad;
    }
    public Advertisement getVerifiedAdvertisement(String advertisementId , String userId){
        if ( advertisementId == null || advertisementId.trim().isEmpty()){
            throw new InvalidAdvertisementIdException("شناسه اگهی نامعتبر است");
        }
        Advertisement ad = advertisementRepository.findByID(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException("آگهی یافت نشد"));
        if (!ad.getOwnerId().equals(userId)) {
            throw new NoAcceessException("شما اجازه دسترسی به این آگهی را ندارید");
        }
        return ad;
    }
    public boolean updateOwnAdvertisement( String userId,Advertisement updatedAd) {

        User user = userRepository.findByID(userId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if ( !user.isEnabled()){
            throw new UserBannedException("کاربر مسدود است");
        }
        if ( updatedAd == null){
            throw new IllegalArgumentException("اگهی جدید نامعتبر است");
        }
        Advertisement ad = getVerifiedAdvertisement(updatedAd.getId() , userId);

        if (updatedAd.getTitle() == null|| updatedAd.getTitle().trim().isEmpty()){
            throw new TitleInvalidException("عنوان آگهی اجباری است");
        }
        if ( updatedAd.getPrice() < 0 ){
            throw new PriceNegativeException("قیمت نامعتبر است");
        }
        Category category = categoryRepository.findByID(updatedAd.getCategoryId())
                .orElseThrow(() -> new InvalidCategoryIdException("دسته بندی وجود ندارد"));

        List<AttributeRule> rules = attributeRulesRepository.findByCategoryId(updatedAd.getCategoryId());
        for (AttributeRule rule : rules) {
            if (rule.isRequired() && !updatedAd.getSpecificAttributes().containsKey(rule.getAttributeName())) {
                throw new IllegalArgumentException("فیلد ضروری '" + rule.getAttributeName() + "' برای این دسته بندی وارد نشده است.");
            }
        }
        ad.setTitle(updatedAd.getTitle());
        ad.setDescription(updatedAd.getDescription());
        ad.setPrice(updatedAd.getPrice());
        ad.setCity(updatedAd.getCity());
        ad.setCategoryId(updatedAd.getCategoryId());
        ad.setSpecificAttributes(updatedAd.getSpecificAttributes());
        ad.setStatus(AdStatus.PENDING_REVIEW);
        advertisementRepository.save(ad);

        return true;
    }

    public boolean deleteOwnAdvertisement(String adId, String userId) {
        User user = userRepository.findByID(userId)
                .orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));

        if ( !user.isEnabled()){
            throw new UserBannedException("کاربر مسدود است");
        }
        Advertisement ad = getVerifiedAdvertisement(adId, userId);
         return advertisementRepository.deleteById(ad.getId());
    }

    public boolean changeAdStatusToSold(String adId, String userId) {
        User user = userRepository.findByID(userId)
                .orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if (!user.isEnabled()) {
            throw new UserBannedException("کاربر مسدود است و اجازه تغییر وضعیت ندارد");
        }
        Advertisement advertisement = getVerifiedAdvertisement(adId , userId);

        if ( advertisement.getStatus() == AdStatus.SOLD){
            return true;
        }
        if ( advertisement.getStatus() != AdStatus.ACTIVE){
            throw new IllegalArgumentException("فقط آگهی های فعال را میتوان به وضعیت فروخته شده تغییر داد");
        }
        advertisement.setStatus(AdStatus.SOLD);
        advertisementRepository.save(advertisement);

        return true;
    }

    public List<Advertisement> searchAndFilterActiveAds(String keyword, String categoryId, String city, Double minPrice, Double maxPrice) {
        return advertisementRepository.findAll().stream()
                .filter(ad -> ad.getStatus() == AdStatus.ACTIVE)
                .filter(ad -> keyword == null || keyword.trim().isEmpty() ||
                        (ad.getTitle() != null && ad.getTitle().contains(keyword)) || (ad.getDescription() != null && ad.getDescription().contains(keyword)))
                .filter(ad -> categoryId == null || categoryId.trim().isEmpty() || (ad.getCategoryId() != null && ad.getCategoryId().equals(categoryId)))
                .filter(ad -> city == null || city.trim().isEmpty() || ad.getCity() != null && ad.getCity().equals(city))
                .filter(ad -> minPrice == null || ad.getPrice() >= minPrice)
                .filter(ad -> maxPrice == null || ad.getPrice() <= maxPrice)
                .collect(Collectors.toList());


    }

    public AdvertisementDetailDTO getActiveAdvertisementDetail(String advertisementId, String userId) {
        User user = userRepository.findByID(userId)
                .orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if (!user.isEnabled()) {
            throw new UserBannedException("شما مسدود هستید و اجازه مشاهده آگهی ها را ندارید");
        }
        Advertisement advertisement = advertisementRepository.findByID(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException("آگهی یافت نشد"));
        boolean isOwner = advertisement.getOwnerId().equals(user.getId());
        boolean isAdmin = user.getRole().equals("ADMIN");

        if (advertisement.getStatus() != AdStatus.ACTIVE && !isOwner && !isAdmin) {
            throw new IllegalArgumentException("این آگهی در حال حاضر فعال نیست.");
        }
        User seller = userRepository.findByID(advertisement.getOwnerId()).orElseThrow( () -> new UserNotFoundException("فروشنده آگهی یافت نشد"));
        List<AttributeRule> rules = attributeRulesRepository.findByCategoryId(advertisement.getCategoryId());
        Map<String , String> rawAttributes = advertisement.getSpecificAttributes();
        List<AdvertisementDetailDTO.AttributeRenderDTO> enrichedAttributes = rules.stream()
                .filter(rule -> rawAttributes.containsKey(rule.getId())) // فقط ویژگی‌هایی که کاربر پر کرده
                .map(rule -> new AdvertisementDetailDTO.AttributeRenderDTO(
                        rule.getAttributeName(), // عنوان فارسی از فایل JSON (مثلا: کارکرد)
                        rawAttributes.get(rule.getId()) // مقدار از دیتابیس آگهی (مثلا: 50000)
                ))
                .collect(Collectors.toList());



        AdvertisementDetailDTO dto = new AdvertisementDetailDTO();
        dto.setId(advertisement.getId());
        dto.setTitle(advertisement.getTitle());
        dto.setDescription(advertisement.getDescription());
        dto.setPrice(advertisement.getPrice());
        dto.setCity(advertisement.getCity()); // اضافه شد
        dto.setCategoryId(advertisement.getCategoryId()); // اضافه شد
        dto.setImageUrls(advertisement.getImageUrls()); // اضافه شد
        dto.setCreatedAt(advertisement.getCreatedAt()); // اضافه شد
        dto.setOwnerId(seller.getId());
        dto.setOwnerName(seller.getUsername()); // یا seller.getName()
        dto.setOwnerAverageRating(seller.getAverageRating());
        dto.setOwner(isOwner);
        dto.setSpecificAttributes(enrichedAttributes);
        return dto;



    }



    public List<AdminPendingAdDTO> getPendingAdvertisementsForAdmin(String userId) {
        User user = userRepository.findByID(userId).orElseThrow( () -> new UserNotFoundException("کاربر یافت نشد"));
        if (!"ADMIN".equals(user.getRole())) {
            throw new NoAcceessException("فقط ادمین دسترسی لازم برای این کار را دارد");
        }

        List<Advertisement> pendingAds = advertisementRepository.findByStatus(AdStatus.PENDING_REVIEW);
        return pendingAds.stream().map(AdminPendingAdDTO::new).collect(Collectors.toList());
    }

    public boolean approveAdvertisement(String advertisementId , String adminId) {
        User user = userRepository.findByID(adminId).orElseThrow( () -> new UserNotFoundException("کاربر یافت نشد"));
        if (!"ADMIN".equals(user.getRole())) {
            throw new NoAcceessException("فقط ادمین دسترسی لازم برای این کار را دارد");
        }
        Advertisement advertisement = advertisementRepository.findByID(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException("آگهی وجود ندارد"));

        if (advertisement.getStatus() != AdStatus.PENDING_REVIEW) {
            throw new AdvertisementStatusException("آگهی در صف تایید نیست و وضعیت آن " + advertisement.getStatus() + " است");
        }
        advertisement.setStatus(AdStatus.ACTIVE);
        advertisementRepository.save(advertisement);

        return true;
    }

    public boolean rejectAdvertisement(String advertisementId, String rejectReason, String adminId) {
        User user = userRepository.findByID(adminId).orElseThrow( () -> new UserNotFoundException("کاربر یافت نشد"));
        if (!"ADMIN".equals(user.getRole())) {
            throw new NoAcceessException("فقط ادمین دسترسی لازم برای این کار را دارد");
        }
        if ( rejectReason.trim().isEmpty()){
            throw new IllegalArgumentException("فیلد دلیل رد اگهی نباید خالی باشد");
        }
        Advertisement advertisement = advertisementRepository.findByID(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException("آگهی وجود ندارد"));
        if ( advertisement.getStatus() == AdStatus.REJECTED ){
            return true;
        }
        if (advertisement.getStatus() != AdStatus.PENDING_REVIEW) {
            throw new AdvertisementStatusException("آگهی در صف تایید نیست و وضعیت آن " + advertisement.getStatus() + " است");
        }
        advertisement.setStatus(AdStatus.REJECTED);
        advertisement.addRejectionReason(rejectReason);
        advertisementRepository.save(advertisement);


        return true;
    }

    public boolean deleteInappropriateAdByAdmin(String advertisementId , String adminId) {
        User user = userRepository.findByID(adminId).orElseThrow( () -> new UserNotFoundException("کاربر یافت نشد"));
        if (!"ADMIN".equals(user.getRole())) {
            throw new NoAcceessException("فقط ادمین دسترسی لازم برای این کار را دارد");
        }
        boolean isRemoved = advertisementRepository.deleteById(advertisementId);

        if (!isRemoved) {
            throw new AdvertisementNotFoundException("آگهی وجود ندارد یا قبلاً حذف شده است");
        }


        return true;
    }
}