# Approval Flow 

---

## Domain Model & Architecture

Bu bölüm, projedeki Enum tanımlarını, Entity sınıflarını, aralarındaki JPA ilişkilerini ve bu mimari kararların teknik gerekçelerini açıklamaktadır.

---

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
* **Barındırdığı Alanlar:** `requestType`, `status`, `title`, `description`, `requester`, `approvalSteps`
* **JPA İlişkileri ve Gerekçeleri:**
    * **`requested_by` (`@ManyToOne`):** Talebi oluşturan kullanıcıyı bağlar (`requester_id` foreign key).
    * **`approvalSteps` (`@OneToMany`):** Talebe bağlı onay adımlarını sıralı tutar.
* **Tasarım Gerekçesi:** Onay akışı `Request` içerisine sabit kolonlarla (`managerApproved`, `hrApproved` vb.) gömülmemiş, `@OneToMany` ilişkisiyle `ApprovalStep` yapısına devredilerek dinamik hale getirilmiştir. `@OrderBy("stepOrder ASC")` kullanılarak adımların sırayla gelmesi sağlanmıştır. Katı iş kuralları (örn: izin gün sınırları) Entity'ye yazılmayıp Service/Policy katmanına bırakılmıştır.

### `ApprovalStep` Entity
* **Barındırdığı Alanlar:** `stepOrder`, `comment`, `actionDate`, `status`, `requiredRole`, `request`, `assignedApprover`
* **JPA İlişkileri ve Gerekçeleri:**
    * **`status` (`@Enumerated(EnumType.STRING)`):** İlgili onay adımının anlık durumunu (`StepStatus`) tutar.
    * **`requiredRole` (`@Enumerated(EnumType.STRING)`):**  Onay adımını tamamlamaya yetkili kullanıcının rolu atanır.
    * **`assignedApprover` (`@ManyToOne`):** Atanmış onaycı gösterilir.
    * **`request` (`@ManyToOne`):** Adımın bağlı olduğu ana talebi temsil eder (`request_id` foreign key).
* **Tasarım Gerekçesi:** Sürecin kaç adımdan oluşacağını, hangi sırayla (`stepOrder`) ilerleyeceğini ve kimlerin onay yetkisine sahip olduğunu dinamik olarak yönetmeyi sağlar. 

### Polimorfik Request DTO Mimarisi

Uygulama, Jackson tabanlı polimorfik ayrıştırma (deserialization) mekanizması sayesinde farklı onay talebi türlerini tek bir ortak endpoint üzerinden dinamik olarak karşılar.

#### Temel Tasarım
- **`BaseRequestDTO`**: `requestType` özelliğine göre gelen JSON verisini ilgili alt sınıflara yönlendiren Jackson `@JsonTypeInfo` ve `@JsonSubTypes` yapılandırmasını barındıran soyut (abstract) temel sınıf.
- **`GenericRequestDTO`**: Temel talep bilgilerinin dışında özel bir alana ihtiyaç duymayan türler (`SOFTWARE_LICENSE`, `TECHNICAL_SUPPORT`) için kullanılan ortak DTO.
- **Alana Özgü DTO'lar**: İlgili tipe özel alanları ve Jakarta Validation kurallarını barındıran türler (`LeaveRequestDTO`, `SalaryAdvanceRequestDTO`).

#### Eşlenen Talep Tipleri

| Talep Tipi (`requestType`) | DTO Sınıfı | Özel Alanlar |
| :--- | :--- | :--- |
| `LEAVE` | `LeaveRequestDTO` | `startDate`, `endDate` |
| `SALARY_ADVANCE` | `SalaryAdvanceRequestDTO` | `requestedAmount` |
| `SOFTWARE_LICENSE` | `GenericRequestDTO` | *(Yok — BaseRequestDTO alanlarını kullanır)* |
| `TECHNICAL_SUPPORT` | `GenericRequestDTO` | *(Yok — BaseRequestDTO alanlarını kullanır)* |

#### Örnek JSON Verisi (`LEAVE`)
```json
{
  "requestType": "LEAVE",
  "title": "Yıllık İzin Talebi",
  "description": "Yaz tatili için izin rica ediyorum.",
  "startDate": "2026-07-10",
  "endDate": "2026-07-20"
}