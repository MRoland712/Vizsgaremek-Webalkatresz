# Car Parts Shop csapatának adatbázis Dokumentációja

## Áttekintés

A **car_parts_shop_fix** egy komplex adatbázis-séma, amely egy autóalkatrész webáruház teljes körű működését támogatja. Az adatbázis tartalmazza a felhasználókezelést, termékkezelést, rendeléskezelést, raktárkezelést, fizetési és szállítási funkciókat.

## Technikai Információk

- **Adatbázis motor:** MySQL 5.7.24
- **Karakterkészlet:** UTF-8
- **Collation:** utf8_general_ci
- **PHP verzió:** 8.3.1
- **Exportálás dátuma:** 2025. november 13.

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
- `is_deleted`, `deleted_at` - Soft delete mezők
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
- `is_deleted`, `deleted_at` - Soft delete

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
- `deleted_at` - Soft delete

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

## Kapcsolatok (Foreign Keys)

Az adatbázis kiterjedt kapcsolati hálózatot használ az adatintegritás biztosítására:

- **users** - központi tábla, több más táblával kapcsolódik
- **parts** - kapcsolódik manufacturers, images, variants, compatibility táblákhoz
- **orders** - kapcsolódik users, order_items, payments, shipping_status táblákhoz
- **warehouse_stock** - összeköti a warehouses és parts táblákat

Minden külső kulcs CASCADE DELETE-tel van konfigurálva, ahol releváns.

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
2. Importálja a `car_parts_shop_fix_2_.sql` fájlt:
   ```bash
   mysql -u felhasználónév -p < car_parts_shop_fix_2_.sql
   ```
3. Ellenőrizze a tárolt eljárások létrejöttét
4. Állítsa be a megfelelő felhasználói jogosultságokat

Vagy

1. Mamp localhost létrehozása
2. phpMyAdmin felületen adatbázis importálása
3. Sikeres adatbázis feltöltése esetén eljárások táblák ellenőrzése

## Karbantartás

### Rendszeres Feladatok

- **Készlet-szinkronizáció:** Ellenőrizze a parts.stock és warehouse_stock közötti konzisztenciát
- **Session tisztítás:** Törölje a lejárt session rekordokat
- **Log archiválás:** Archiválja a régi login_logs és user_logs bejegyzéseket
- **Soft delete tisztítás:** Döntse el a véglegesen törlendő rekordok sorsát

### Indexek

Az adatbázis optimalizált indexekkel rendelkezik a következő mezőkön:
- Külső kulcsok
- E-mail címek (UNIQUE)
- Tokenek (UNIQUE)
- Időbélyegek (logged_in_at)

## Tudnivaló

Ez a séma egy autóalkatrész webáruház belső használatára készült.

## Verzió

**Aktuális verzió:** 2.0
**Utolsó módosítás:** 2025. november 13.

