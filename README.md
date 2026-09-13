# Approval Flow - Dinamik Talep ve Onay Yönetim Sistemi

## Proje Hakkında ve Amaç

Bu proje, gerçek hayat iş senaryolarına dayalı **"Dinamik Talep ve Onay Yönetim Sistemi"** gereksinimlerini karşılamak üzere geliştirilmiştir. Projenin temel amacı, sadece CRUD (Create, Read, Update, Delete) operasyonları gerçekleştiren statik bir uygulama yazmak değil; bir kurum içerisindeki farklı talep türlerini, bu türlere özel onay kurallarını ve dinamik onay süreçlerini uçtan uca yönetebilen, genişletilebilir ve kurumsal standartlara uygun bir mimari kurmaktır.

### Projenin Odak Noktaları ve Kapsamı

* **Dinamik İş Akışı ve Polimorfizm:** İzin, avans, yazılım lisansı ve teknik destek gibi farklı veri yapılarına ve iş kurallarına sahip talep türlerinin, mevcut sistemi bozmadan (`Open/Closed Principle`) sisteme modüler bir şekilde eklenebileceği esnek bir mimari sunmak.
* **Katmanlı Mimari ve SOLID Prensipleri:** Controller, Service, Repository, Policy ve Mapper katmanlarının sorumluluklarını net bir şekilde ayırarak; iş kurallarını tek bir sınıfa yığmadan interface ve strateji kalıpları (Strategy Pattern) üzerinden yönetmek.
* **Gelişmiş DTO ve Entity Yönetimi:** İstemci ile iletişimde DTO ve Jakarta Validation yapılarını kullanarak güvenli veri transferi sağlamak; veritabanı seviyesinde ise JPA/Hibernate ve MapStruct ile polimorfik nesne haritalamasını gerçekleştirmek.
* **Süreç ve Geçmiş Takibi:** Taleplerin taslak aşamasından (`DRAFT`) onaylanma (`APPROVED`) veya reddedilme (`REJECTED`) aşamasına kadar olan tüm mikro onay adımlarını (`ApprovalStep`), atanan kişileri, rol yetkilerini ve işlem açıklamalarını kayıt altına almak.

## 1. Enum Yapıları ve Tasarım Gerekçeleri

Tüm Enum tanımları veritabanında `@Enumerated(EnumType.STRING)` olarak saklanmaktadır. Bu sayede Enum sırası (`ordinal`) değişse dahi veritabanındaki veriler bozulmaz ve tablo verileri doğrudan okunabilir kalır.

### `DepartmentType`
* **Değerler:** `HUMAN_RESOURCES`, `FINANCE`, `INFORMATION_TECHNOLOGIES`, `SOFTWARE_DEVELOPMENT`
* **Tasarım Gerekçesi:** Şirket bünyesindeki ana departmanları temsil eder. Departman isimleri veritabanında ayrı bir tablo yönetimi gerektirmeyen sabit yapılar olduğu için Enum olarak tanımlanmıştır.

### `UserRole`
* **Değerler:** `SOFTWARE_DEVELOPER`, `DEPARTMENT_MANAGER`, `HR_SPECIALIST`, `FINANCE_SPECIALIST`,`IT_SPECIALIST`, `ADMIN`
* **Tasarım Gerekçesi:** Kullanıcıların sistemdeki yetki seviyelerini belirler. Bir kullanıcının aynı anda birden fazla role sahip olabilir. 
### `RequestType`
* **Değerler:** `LEAVE`, `SALARY_ADVANCE`, `SOFTWARE_LICENSE`, `TECHNICAL_SUPPORT`
* **Tasarım Gerekçesi:** Oluşturulabilecek talep türlerini belirler. Her talep türünün ileride farkı onay adımlarına yönlendirilebilmesi ve iş kurallarının (Policy/Service katmanında) ayrıştırılabilmesi amacıyla Enum yapısında tutulmuştur.

### `RequestStatus`
* **Değerler:** `DRAFT`, `IN_APPROVAL`, `APPROVED`, `REJECTED`, `CANCELLED`
* **Tasarım Gerekçesi:** Talebin genel (makro) yaşam döngüsünü temsil eder. Talebin taslakta mı, onay sürecinde mi yoksa sonuçlanmış mı olduğunu takip etmeyi sağlar.

### `StepStatus`
* **Değerler:** `WAITING`, `PENDING`, `APPROVED`, `REJECTED`
* **Tasarım Gerekçesi:** Bir talebin içerisindeki her bir onay adımının (mikro) anlık durumunu tutar. Talebin genel durumu `IN_APPROVAL` iken, sırası gelen adım `PENDING`, henüz sırası gelmeyen adımlar ise `WAITING` durumunda bekler.

---

## 2. Entity Katmanı, İlişkiler ve Mimari Tercihler

### `BaseEntity` (`@MappedSuperclass`)
* **Barındırdığı Alanlar:** `id`, `createdAt`, `updatedAt`
* **Tasarım Gerekçesi:** Tüm veritabanı tablolarında ortak olan birincil anahtar (Primary Key) ve zaman damgası (Timestamp) alanlarını tek bir merkezden yönetip kod tekrarını önlemek amacıyla `@MappedSuperclass` olarak tasarlanmıştır.

### `User` Entity
* **Barındırdığı Alanlar:** `firstName`, `lastName`, `email`, `passwordHash`, `userRoles`, `departmentType`
* **JPA İlişkileri ve Gerekçeleri:**
    * **`userRoles` (`@ElementCollection` + `@CollectionTable`):** `user_roles` adında bir ara tablo oluşturur. Bir kullanıcının aynı anda birden fazla role (`UserRole`) sahip olabilmesini sağlar.
    * **`departmentType` (`@Enumerated(EnumType.STRING)`):** Departman türünü tip güvenli şekilde tutar.

### `Request` Entity
* **Barındırdığı Alanlar:** `requestType`, `status`, `title`, `description`, `requestedBy`, `approvalSteps`,`detail`
* **JPA İlişkileri ve Gerekçeleri:**
    * **`requested_by` (`@ManyToOne`):** Talebi oluşturan kullanıcıyı bağlar (`requester_id` foreign key).
    * **`approvalSteps` (`@OneToMany`):** Talebe bağlı onay adımlarını sıralı tutar.
    * **`detail` (`@OneToOne`):** Talebin detaylarını 1-1 ilişkili olarak ayrı tabloda saklar.
* **Tasarım Gerekçesi:** Onay akışı `Request` içerisine sabit kolonlarla (`managerApproved`, `hrApproved` vb.) gömülmemiş, `@OneToMany` ilişkisiyle `ApprovalStep` yapısına devredilerek dinamik hale getirilmiştir. `@OrderBy("stepOrder ASC")` kullanılarak adımların sırayla gelmesi sağlanmıştır. Katı iş kuralları (örn: izin gün sınırları) Entity'ye yazılmayıp Service/Policy katmanına bırakılmıştır.Detail abstract classtan extend edilen detail objeleri içerebilir. Zorunlu bir alan değildir.

### `ApprovalStep` Entity
* **Barındırdığı Alanlar:** `stepOrder`, `comment`, `actionDate`, `status`, `requiredRole`, `request`, `assignedApprover`
* **JPA İlişkileri ve Gerekçeleri:**
    * **`status` (`@Enumerated(EnumType.STRING)`):** İlgili onay adımının anlık durumunu (`StepStatus`) tutar.
    * **`requiredRole` (`@Enumerated(EnumType.STRING)`):**  Onay adımını tamamlamaya yetkili kullanıcının rolu atanır.
    * **`assignedApprover` (`@ManyToOne`):** Atanmış onaycı gösterilir.
    * **`request` (`@ManyToOne`):** Adımın bağlı olduğu ana talebi temsil eder (`request_id` foreign key).
* **Tasarım Gerekçesi:** Sürecin kaç adımdan oluşacağını, hangi sırayla (`stepOrder`) ilerleyeceğini ve kimlerin onay yetkisine sahip olduğunu dinamik olarak yönetmeyi sağlar. 

### `RequestDetail` Entity
* **Barındırdığı Alanlar:** `request`
* **JPA İlişkileri ve Gerekçeleri:**
    * **`@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`:** Her somut alt sınıf için veritabanında ayrı bir tablo oluşturulmasını sağlar. Polimorfik sorguları desteklerken soyut sınıf için boş bir üst tablo oluşmasını engeller.
    * **`request` (`@OneToOne` + `@MapsId`):** `Request` ana tablosu ile birebir ilişki kurar. `@MapsId` sayesinde kendi Primary Key değerini üretmek yerine bağlı olduğu `Request` nesnesinin `id` değerini hem PK hem FK (`request_id`) olarak kullanır. `nullable = false` ile ilişkisiz detay kaydı oluşması engellenir.
* **Tasarım Gerekçesi:** Farklı talep türlerinin ortak davranışlarını tek bir üst çatı altında toplar. Sistemdeki onay mantığını veri yapısından ayırarak yeni talep tiplerinin modüler bir şekilde sisteme eklenmesine olanak tanır.

### `LeaveRequestDetail` Entity
* **Barındırdığı Alanlar:** `startDate`, `endDate`, `request` *(RequestDetail'den türetilen)*
* **JPA İlişkileri ve Gerekçeleri:**
    * **`startDate` / `endDate` (`@Column(nullable = false)`):** İznin başlangıç ve bitiş tarihlerini tutar.
* **Tasarım Gerekçesi:** İzin süreçlerine özel tarih bilgilerini ana `Request` tablosunu kirletmeden kendi bünyesinde izole eder. `RequestDetail` sınıfını genişleterek polimorfik onay mimarisine doğrudan dahil olur.

### `SalaryAdvanceRequestDetail` Entity
* **Barındırdığı Alanlar:** `amount`, `reason`, `request` *(RequestDetail'den türetilen)*
* **JPA İlişkileri ve Gerekçeleri:**
    * **`amount` (`@Column(nullable = false)`):** Talep edilen avans tutarını tutar.
* **Tasarım Gerekçesi:** Finansal taleplere özgü verileri kapsapsüller (encapsulation). 

---
## 3. Polimorfik Request DTO Mimarisi


Uygulama, Jackson tabanlı polimorfik ayrıştırma (deserialization) mekanizması sayesinde farklı onay talebi türlerini tek bir ortak endpoint üzerinden dinamik olarak karşılar. Ana DTO yapısı (`RequestDTO`) üst seviye alanları tutarken, talebe özel detay verileri `BaseRequestDetailDTO` soyut sınıfı üzerinden polimorfik olarak kapsüllenir.

#### Temel Tasarım
- **`RequestDTO`**: Ana istek gövdesini temsil eder. `requestType`, `title`, `description` gibi ortak bilgileri ve `@Valid` ile sarmalanmış polimorfik `detail` nesnesini barındırır.
- **`BaseRequestDetailDTO`**: Jackson `@JsonTypeInfo` ve `@JsonSubTypes` anotasyonları ile yapılandırılmış soyut (abstract) detay sınıfıdır. İstek gövdesinde dış alan olarak gelen (`JsonTypeInfo.As.EXTERNAL_PROPERTY`) `requestType` değerine göre ilgili alt detay DTO sınıfına türetilir. Detay içermeyen istekler için varsayılan olarak `Void.class` eşlemesi kullanılır.
- **Detay DTO'ları (`shared`)**: İlgili talep türüne özel alanları ve Jakarta Validation kurallarını (`@FutureOrPresent`, `@Positive` vb.) barındıran somut sınıflardır (`LeaveRequestDetailDTO`, `SalaryAdvanceRequestDetailDTO`).
- **Yanıt DTO'ları (`response`)**: İstemciye dönülecek olan `RequestResponseDTO` ve `ApprovalStepResponseDTO` nesneleridir. Detay nesnesi yine `BaseRequestDetailDTO` üzerinden esnek bir şekilde sunulur.

#### Eşlenen Talep ve Detay DTO Tipleri

| Talep Tipi (`requestType`) | Detay DTO Sınıfı (`detail`) | Özel Alanlar ve Doğrulama Kuralları |
| :--- | :--- | :--- |
| `LEAVE` | `LeaveRequestDetailDTO` | `startDate` (Gelecek/Bugün), `endDate` (Gelecek/Bugün) |
| `SALARY_ADVANCE` | `SalaryAdvanceRequestDetailDTO` | `amount` (Pozitif Değer) |
| `SOFTWARE_LICENSE` | *(Yok — `null`)* | Detay nesnesi içermez (`detail: null`) |
| `TECHNICAL_SUPPORT` | *(Yok — `null`)* | Detay nesnesi içermez (`detail: null`) |

#### Örnek JSON Verisi (`LEAVE`)
```json
{
  "requestType": "LEAVE",
  "title": "Yıllık İzin Talebi",
  "description": "Yaz tatili için izin rica ediyorum.",
  "detail": {
    "startDate": "2026-07-10",
    "endDate": "2026-07-20"
  }
}
```

---

## 4. Mapper Katmanı (MapStruct Mimarisi)

Uygulamanın DTO (`RequestDTO`, `RequestResponseDTO`, `RequestDetailDTO`) ve Entity (`Request`, `RequestDetail`) katmanları arasındaki veri dönüşümleri, derleme anında (compile-time) tip güvenli kod üreten **MapStruct** kütüphanesi ile yönetilmektedir.

Mapper arayüzleri, Spring Dependency Injection (DI) mimarisine tam uyum sağlamak amacıyla `@Mapper(componentModel = "spring")` olarak yapılandırılmıştır.

### Mapper Bileşenleri ve Sorumlulukları

#### `RequestDetailMapper`
* **Sorumluluk:** Polimorfik detay verilerinin (`RequestDetail` alt sınıfları) DTO ve Entity katmanları arasında çift yönlü dönüşümünü sağlar.
* **Teknik Detay:** MapStruct'ın `@SubclassMappings` ve `@SubclassMapping` anotasyonlarını kullanarak runtime esnasında ilgili nesnenin somut tipine (`LeaveRequestDetail` veya `SalaryAdvanceRequestDetail`) göre doğru mapper metodunu tetikler. Jackson tarafındaki `@JsonSubTypes` eşlemesinin Service ve Entity katmanındaki tam karşılığıdır.

#### `ApprovalStepMapper`
* **Sorumluluk:** Talebe bağlı onay adımlarının (`ApprovalStep`) istemciye sunulacak yanıt modeline (`ApprovalStepResponseDTO`) dönüştürülmesini sağlar.
* **Teknik Detay:** Entity üzerindeki `assignedApprover` (`User`) nesnesinin `id` değerini `assignedApproverId` alanına düzleştirerek (flattening) aktarır. Ayrıca `List<ApprovalStep>` koleksiyonlarını `List<ApprovalStepResponseDTO>` yapısına tekil metodu yeniden kullanarak (reusable) dönüştürür.

#### `RequestMapper` (Ana Mapper)
* **Sorumluluk:** Ana talep nesnesinin (`Request`) `RequestDTO` ve `RequestResponseDTO` dönüşümlerini orkestre eden köprü bileşendir.
* **Teknik Detay:**
    * `uses = {RequestDetailMapper.class, ApprovalStepMapper.class}` parametresi ile alt mapper'ları bünyesine dahil eder.
    * **DTO -> Entity (`toEntity`):** `id`, `status`, `requestedBy` ve `approvalSteps` gibi iş mantığı / Service katmanında set edilecek alanları `@Mapping(target = "...", ignore = true)` ile es geçer. DTO üzerindeki `detail` alanını Entity'deki `requestDetail` alanına bağlar.
    * **Entity -> ResponseDTO (`toResponseDto`):** Sub-mapper'ları tetikleyerek hem polimorfik detay yapısının hem de onay adımları listesinin kayıpsız bir şekilde yanıt DTO'suna aktarılmasını sağlar.

---

### Mapper Eşleşme Matrisi

| Kaynak Nesne (Source) | Hedef Nesne (Target) | Sorumlu Mapper | Özel Dönüşüm / Mantık                                                          |
| :--- | :--- | :--- |:-------------------------------------------------------------------------------|
| `RequestDTO` | `Request` | `RequestMapper` | Business alanları `ignore` edilir.                                             |
| `Request` | `RequestResponseDTO` | `RequestMapper` | `requestedBy.id` -> `requestedBy` düzleştirilir, alt mapper'lar tetiklenir.    |
| `RequestDetail` *(Abstract)* | `RequestDetailDTO` *(Abstract)* | `RequestDetailMapper` | `@SubclassMapping` ile somut sınıfa (`Leave` / `SalaryAdvance`) yönlendirilir. |
| `ApprovalStep` | `ApprovalStepResponseDTO` | `ApprovalStepMapper` | `assignedApprover.id` -> `assignedApproverId` dönüşümü yapılır.                |
| `List<ApprovalStep>` | `List<ApprovalStepResponseDTO>` | `ApprovalStepMapper` | Koleksiyon elemanları tekil `toDto` çağrısı ile dönüştürülür.                  |