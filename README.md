# Car Parts Shop csapatának adatbázis Dokumentációja

## Áttekintés

A **car_parts_shop_fix** egy komplex adatbázis-séma, amely egy autóalkatrész webáruház teljes körű működését támogatja. Az adatbázis tartalmazza a felhasználókezelést, termékkezelést, rendeléskezelést, raktárkezelést, fizetési és szállítási funkciókat.

## Technikai Információk

- **Adatbázis motor:** MySQL 5.7.24
- **Karakterkészlet:** UTF-8
- **Collation:** utf8_general_ci
- **PHP verzió:** 8.3.1
- **Exportálás dátuma:** 2025. november 16.

## Adatbázis Struktúra

### Felhasználókezelés

#### users
A rendszer felhasználóinak központi táblája.

**Főbb mezők:**
- `id` - Elsődleges kulcs
- `guid` - Globálisan egyedi azonosító
- `email` - E-mail cím (egyedi)
- `username` - Felhasználónév
- `password` - Titkosított jelszó
- `first_name`, `last_name` - Név adatok
- `phone` - Telefonszám
- `role` - Felhasználói szerepkör (user/admin)
- `is_active` - Aktív státusz
- `email_verified`, `phone_verified` - Verifikációs státuszok
- `auth_secret` - Autentikációs titkos kulcs
- `registration_token` - Regisztrációs token
- `failed_login_attempts` - Sikertelen bejelentkezési kísérletek száma
- `locked_until` - Fiókzárolás időpontja
- `is_deleted` - Soft delete jelző
- `deleted_at` - Soft delete időbélyeg
- `created_at`, `updated_at`, `last_login` - Időbélyegek

#### addresses
Felhasználók címadatai (számlázási és szállítási címek).

**Főbb mezők:**
- `user_id` - Külső kulcs a users táblára
- `first_name`, `last_name` - Címzett neve
- `company` - Cégnév (opcionális)
- `tax_number` - Adószám (opcionális)
- `country`, `city`, `zip_code`, `street` - Cím komponensek
- `is_default` - Alapértelmezett cím jelölése
- `is_deleted` - Soft delete jelző
- `deleted_at` - Soft delete időbélyeg

#### email_verifications
E-mail cím verifikációs tokenek kezelése.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `token` - 6 karakteres verifikációs kód
- `verified` - Verifikálva-e
- `sent_at`, `verified_at` - Időbélyegek

#### password_resets
Jelszó-visszaállítási tokenek.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `token` - Visszaállítási token
- `expires_at` - Lejárati idő
- `used` - Felhasználva-e

#### sessions
Felhasználói munkamenetek kezelése.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `token` - Session token
- `expires_at` - Lejárati idő
- `revoked` - Visszavont-e

#### user_twofa
Kétfaktoros autentikáció beállításai.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `twofa_enabled` - 2FA engedélyezve-e
- `twofa_secret` - 2FA titkos kulcs
- `recovery_codes` - Helyreállítási kódok

#### login_logs
Bejelentkezési események naplózása.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `user_agent` - Böngésző azonosító
- `logged_in_at` - Bejelentkezés időpontja

#### user_logs
Felhasználói műveletek auditálása.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `action` - Művelet típusa
- `details` - Részletes leírás
- `created_at` - Időbélyeg

### Termékkezelés

#### manufacturers
Alkatrész-gyártók adatai.

**Főbb mezők:**
- `name` - Gyártó neve
- `country` - Ország
- `is_deleted` - Soft delete jelző
- `deleted_at` - Soft delete időbélyeg

#### parts
Alkatrészek központi táblája.

**Főbb mezők:**
- `manufacturer_id` - Külső kulcs
- `sku` - Egyedi cikkszám
- `name` - Termék neve
- `category` - Kategória
- `price` - Ár
- `stock` - Készlet
- `status` - Státusz (available/unavailable)
- `is_active` - Aktív-e
- `is_deleted` - Soft delete jelző
- `deleted_at` - Soft delete időbélyeg

#### part_images
Alkatrészekhez tartozó képek.

**Főbb mezők:**
- `part_id` - Külső kulcs
- `url` - Kép URL
- `is_primary` - Elsődleges kép-e

#### part_variants
Alkatrész-variánsok (méret, szín, stb.).

**Főbb mezők:**
- `part_id` - Külső kulcs
- `name` - Variáns neve
- `value` - Variáns értéke
- `additional_price` - További ár

#### part_compatibility
Alkatrész-kompatibilitás járműmodellekkel.

**Főbb mezők:**
- `part_id` - Külső kulcs az alkatrészre
- `model_id` - Külső kulcs a járműmodellre
- `engine_type` - Motor típus
- `transmission` - Sebességváltó típus

#### reviews
Termékértékelések.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `part_id` - Külső kulcs
- `rating` - Értékelés (1-5)
- `comment` - Szöveges vélemény
- `is_deleted` - Soft delete jelző
- `deleted_at` - Soft delete időbélyeg

### Járműadatok

#### vehicle_brands
Járműmárkák.

**Főbb mezők:**
- `name` - Márka neve

#### vehicle_models
Járműmodellek.

**Főbb mezők:**
- `brand_id` - Külső kulcs a márkára
- `name` - Modell neve
- `year_from`, `year_to` - Gyártási időszak

#### motor_brands
Motormárkák.

**Főbb mezők:**
- `name` - Márka neve

#### motor_models
Motormodellek.

**Főbb mezők:**
- `brand_id` - Külső kulcs
- `name` - Modell neve
- `year_from`, `year_to` - Gyártási időszak

### Rendeléskezelés

#### orders
Rendelések.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `status` - Rendelés állapota (pending/processing/completed/cancelled)
- `is_deleted` - Soft delete jelző
- `deleted_at` - Soft delete időbélyeg
- `created_at` - Létrehozás időpontja

#### order_items
Rendelési tételek.

**Főbb mezők:**
- `order_id` - Külső kulcs
- `part_id` - Külső kulcs
- `quantity` - Mennyiség
- `price` - Egységár

#### order_logs
Rendelési státuszváltozások naplója.

**Főbb mezők:**
- `order_id` - Külső kulcs
- `old_status` - Régi állapot
- `new_status` - Új állapot
- `changed_at` - Változás időpontja

#### cart_items
Bevásárlókosár tételek.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `part_id` - Külső kulcs
- `quantity` - Mennyiség
- `added_at` - Hozzáadás időpontja

### Fizetési Rendszer

#### payments
Fizetési tranzakciók.

**Főbb mezők:**
- `order_id` - Külső kulcs
- `amount` - Összeg
- `method` - Fizetési mód
- `status` - Fizetés állapota
- `paid_at` - Fizetés időpontja

#### refunds
Visszatérítések.

**Főbb mezők:**
- `payment_id` - Külső kulcs
- `amount` - Visszatérítendő összeg
- `reason` - Indoklás
- `refunded_at` - Visszatérítés időpontja

#### invoices
Számlák.

**Főbb mezők:**
- `order_id` - Külső kulcs
- `pdf_url` - Számla PDF elérési útja
- `created_at` - Létrehozás időpontja

### Szállítás

#### shipping_methods
Szállítási módok.

**Főbb mezők:**
- `name` - Szállítási mód neve
- `price` - Szállítási díj
- `duration` - Szállítási idő

#### shipping_status
Szállítási állapotok nyomon követése.

**Főbb mezők:**
- `order_id` - Külső kulcs
- `status` - Szállítási állapot
- `tracking_no` - Követési szám
- `updated_at` - Utolsó frissítés

### Raktárkezelés

#### warehouses
Raktárak.

**Főbb mezők:**
- `name` - Raktár neve
- `location` - Helyszín
- `is_deleted` - Soft delete jelző
- `deleted_at` - Soft delete időbélyeg

#### warehouse_stock
Raktárkészletek.

**Főbb mezők:**
- `warehouse_id` - Külső kulcs
- `part_id` - Külső kulcs
- `quantity` - Mennyiség
- `updated_at` - Utolsó frissítés

#### stock_logs
Készletmozgások naplója.

**Főbb mezők:**
- `part_id` - Külső kulcs
- `change_amount` - Változás mértéke
- `reason` - Indoklás

### Egyéb Funkciók

#### product_comparisons
Termék-összehasonlítási listák.

**Főbb mezők:**
- `user_id` - Külső kulcs
- `created_at` - Létrehozás időpontja

#### product_comparison_items
Összehasonlítási listában szereplő termékek.

**Főbb mezők:**
- `comparison_id` - Külső kulcs
- `part_id` - Külső kulcs

## Tárolt Eljárások

### Felhasználókezelési Eljárások

#### createUser
Új felhasználó létrehozása a szükséges kezdeti beállításokkal.

**Paraméterek:**
- `p_email` - E-mail cím
- `p_username` - Felhasználónév
- `p_password` - Titkosított jelszó
- `p_first_name`, `p_last_name` - Név
- `p_phone` - Telefonszám
- `p_role` - Szerepkör
- `p_auth_secret` - Autentikációs kulcs
- `p_registration_token` - Regisztrációs token

**Visszatérés:** Az új felhasználó ID-ja

#### getUserByEmail
Felhasználó lekérdezése e-mail cím alapján.

**Paraméterek:**
- `p_email` - E-mail cím

#### getUserById
Felhasználó lekérdezése azonosító alapján.

**Paraméterek:**
- `p_user_id` - Felhasználó ID

#### readUsers
Az összes felhasználó listázása.

#### updateUser
Felhasználói adatok módosítása.

**Paraméterek:**
- `p_user_id` - Felhasználó ID
- További frissítendő mezők

#### softDeleteUser
Felhasználó soft delete-tel való törlése.

**Paraméterek:**
- `p_user_id` - Felhasználó ID

#### softDeleteUserAndAddresses
Felhasználó és hozzá tartozó címek soft delete-tel való törlése.

**Paraméterek:**
- `p_user_id` - Felhasználó ID

#### user_login
Bejelentkezési folyamat kezelése (last_login frissítése).

**Paraméterek:**
- `p_email` - E-mail cím

### Címkezelési Eljárások

#### createAddress
Új cím létrehozása a felhasználóhoz.

**Paraméterek:**
- `p_user_id` - Felhasználó ID
- `p_first_name`, `p_last_name` - Címzett neve
- `p_company` - Cégnév (opcionális)
- `p_tax_number` - Adószám (opcionális)
- `p_country`, `p_city`, `p_zip_code`, `p_street` - Cím komponensek
- `p_is_default` - Alapértelmezett cím-e

**Visszatérés:** Az új cím ID-ja

**Megjegyzés:** Ha az új cím alapértelmezett, akkor a felhasználó többi címe automatikusan elveszti az alapértelmezett státuszát.

#### readAddressesByUserId
Felhasználó összes címének lekérdezése.

**Paraméterek:**
- `p_user_id` - Felhasználó ID

**Visszatérés:** A felhasználó összes aktív címe, alapértelmezett cím előre rendezve

#### getAddressById
Egy konkrét cím lekérdezése ID alapján.

**Paraméterek:**
- `p_address_id` - Cím ID

#### updateAddress
Cím adatainak módosítása.

**Paraméterek:**
- `p_address_id` - Cím ID
- `p_first_name`, `p_last_name` - Címzett neve
- `p_company` - Cégnév
- `p_tax_number` - Adószám
- `p_country`, `p_city`, `p_zip_code`, `p_street` - Cím komponensek
- `p_is_default` - Alapértelmezett cím-e

**Megjegyzés:** Az alapértelmezett cím beállítás itt is automatikusan kezelt.

#### softDeleteAddress
Cím soft delete-tel való törlése.

**Paraméterek:**
- `p_address_id` - Cím ID

#### getDefaultAddress
Felhasználó alapértelmezett címének lekérdezése.

**Paraméterek:**
- `p_user_id` - Felhasználó ID

#### setAddressAsDefault
Egy létező cím beállítása alapértelmezettként.

**Paraméterek:**
- `p_address_id` - Cím ID

**Megjegyzés:** A felhasználó többi címe automatikusan elveszti az alapértelmezett státuszát.

### Alkatrész kezelési Eljárások

#### updatePart
Alkatrész adatainak módosítása.

**Paraméterek:**
- `p_part_id` - Alkatrész ID
- `p_manufacturer_id` - Gyártó ID
- `p_sku` - Cikkszám
- `p_name` - Termék neve
- `p_category` - Kategória
- `p_price` - Ár
- `p_stock` - Készlet
- `p_status` - Státusz
- `p_is_active` - Aktív-e

**Megjegyzés:** Automatikusan frissíti az `updated_at` mezőt.

#### softDeletePart
Alkatrész soft delete-tel való törlése.

**Paraméterek:**
- `p_part_id` - Alkatrész ID

**Megjegyzés:** A törölt alkatrészek megmaradnak az adatbázisban, csak a `deleted_at` mező kerül beállításra. Ez biztosítja, hogy a múltbeli rendelések és számlák továbbra is érvényesek maradjanak.

## Kapcsolatok (Foreign Keys)

Az adatbázis kiterjedt kapcsolati hálózatot használ az adatintegritás biztosítására:

- **users** - központi tábla, több más táblával kapcsolódik
- **parts** - kapcsolódik manufacturers, images, variants, compatibility táblákhoz
- **orders** - kapcsolódik users, order_items, payments, shipping_status táblákhoz
- **warehouse_stock** - összeköti a warehouses és parts táblákat

Minden külső kulcs CASCADE DELETE-tel van konfigurálva, ahol releváns.

## Soft Delete Stratégia

Az adatbázis következetes soft delete mechanizmust alkalmaz a kritikus adatok megőrzésére:

### Soft Delete-tel rendelkező táblák:
- **users** - Felhasználók (GDPR, jogszabályi követelmények)
- **addresses** - Címek (rendelési history)
- **parts** - Alkatrészek (rendelési history, számlák)
- **orders** - Rendelések (kötelező megőrzés)
- **reviews** - Értékelések (moderálás, history)
- **manufacturers** - Gyártók (termék referenciák)
- **warehouses** - Raktárak (készlet history)

### Implementáció:
Minden soft delete-es tábla két mezővel rendelkezik:
- `is_deleted` (TINYINT) - Logikai jelző
- `deleted_at` (DATETIME) - Törlés időpontja

### Használat:
```sql
-- Aktív rekordok lekérdezése
WHERE is_deleted = 0 AND deleted_at IS NULL

-- Törölt rekordok lekérdezése
WHERE is_deleted = 1 OR deleted_at IS NOT NULL
```

## Biztonság

Az adatbázis több biztonsági mechanizmust implementál:

1. **Jelszókezelés:** Titkosított jelszótárolás
2. **Kétfaktoros autentikáció:** user_twofa tábla
3. **Session kezelés:** Token-alapú munkamenet-kezelés
4. **Fiókbiztonság:** Sikertelen bejelentkezések számolása, fiókzárolás
5. **Auditálás:** login_logs és user_logs táblák
6. **Soft Delete:** Adatok helyreállítható törlése
7. **E-mail verifikáció:** Regisztrációs token és email_verifications

## Telepítés

1. Hozzon létre egy MySQL adatbázist
2. Importálja a `car_parts_shop_fix.sql` fájlt:
   ```bash
   mysql -u felhasználónév -p < car_parts_shop_fix.sql
   ```
3. Futtassa le a soft delete mezők hozzáadását:
   ```bash
   mysql -u felhasználónév -p car_parts_shop_fix < add_soft_delete_columns.sql
   ```
4. Ellenőrizze a tárolt eljárások létrejöttét
5. Állítsa be a megfelelő felhasználói jogosultságokat

Vagy

1. MAMP localhost létrehozása
2. phpMyAdmin felületen adatbázis importálása
3. Soft delete mezők hozzáadása SQL script futtatásával
4. Sikeres adatbázis feltöltése esetén eljárások és táblák ellenőrzése

## Karbantartás

### Rendszeres Feladatok

- **Készlet-szinkronizáció:** Ellenőrizze a parts.stock és warehouse_stock közötti konzisztenciát
- **Session tisztítás:** Törölje a lejárt session rekordokat
- **Log archiválás:** Archiválja a régi login_logs és user_logs bejegyzéseket
- **Soft delete tisztítás:** Döntse el a véglegesen törlendő rekordok sorsát (GDPR szerint általában 30-90 nap után)

### Indexek

Az adatbázis optimalizált indexekkel rendelkezik a következő mezőkön:
- Külső kulcsok
- E-mail címek (UNIQUE)
- Tokenek (UNIQUE)
- Időbélyegek (logged_in_at)
- Soft delete mezők (is_deleted)

## Tudnivaló

Ez a séma egy autóalkatrész webáruház belső használatára készült. A soft delete mechanizmus biztosítja, hogy az üzleti és jogszabályi követelményeknek megfelelően őrizzük meg a kritikus adatokat.

## Változásnapló

### 2.2 verzió (2025. november 16.)
- Soft delete mezők (`is_deleted`, `deleted_at`) hozzáadása: parts, orders, reviews, manufacturers, warehouses táblákhoz
- Címkezelési stored procedure-ök implementálása (createAddress, readAddressesByUserId, getAddressById, updateAddress, softDeleteAddress, getDefaultAddress, setAddressAsDefault)
- Alkatrész kezelési stored procedure-ök: updateParts, softDeleteParts, createParts, getParts, getPartsByManufacturerId, getPartsById
- README dokumentáció frissítése

### 2.1 verzió (2025. november 13.)
- Alapvető adatbázis struktúra
- Felhasználókezelési stored procedure-ök
- Biztonság funkcionalitás implementálása

## Verzió

**Aktuális verzió:** 2.2
**Utolsó módosítás:** 2025. november 16.