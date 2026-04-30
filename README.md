# CarComps – Autóalkatrész Webáruház

A **CarComps** egy Java EE alapú autóalkatrész webáruház vizsgaremek projekt, járműkompatibilitás-kezeléssel és teljes körű e-commerce funkcionalitással.

## A Teljes Weboldal a [carcomps.hu](https://carcomps.hu) címen elérhető 

## Tech Stack

|Réteg|Technológia|
|-|-|
|Backend|Java 17, JAX-RS (RESTEasy)|
|Alkalmazásszerver|WildFly 26.1.1|
|Adatbázis|MySQL 5.7 (`car\\\_parts\\\_shop\\\_fix`)|
|ORM|JPA / EntityManager|
|Build|Maven|
|Frontend|Angular|
|Auth|JWT + Google Authenticator (TOTP)|
|Adatbázis-connector|MySQL Connector/J 8.0.23|


## Architektúra

A projekt szigorú rétegelt (layered) architektúrát követ:

```
Tárolt eljárások (Stored Procedures)
        ↓
    Model réteg
        ↓
   Service réteg
        ↓
  Controller (JAX-RS REST végpontok)
```

A konfiguráció egy külön `config` csomagban van kezelve.


## Főbb funkciók

* Felhasználókezelés (regisztráció, bejelentkezés, 2FA, email-verifikáció, jelszó-visszaállítás)
* Termékkezelés (alkatrészek, gyártók, képek, járműkompatibilitás)
* Rendeléskezelés (kosár, fizetés, PDF számla generálás, email értesítések)
* Garázs funkció (felhasználó járműveinek mentése)
* Admin felület
* Soft delete minden kritikus entitáson (`is_deleted`, `deleted_at`)


## Adatbázis

Az adatbázisséma a `car_parts_shop_fix.sql` fájlban található. Importálás:

```bash
mysql -u root -p < car_parts_shop_fix.sql
```

Minden adatbázis-művelet tárolt eljárásokon keresztül történik.


## Fejlesztői környezet

* **IDE:** IntelliJ IDEA / NetBeans
* **API tesztelés:** Postman
* **Verziókezelés:** GitHub
* **Produkció:** Cloudflare tunnel → [carcomps.hu](https://carcomps.hu)


## Fejlesztők

|Név|Szerep|
|-|-|
|Kis-Borbás Dorián|Backend, Szerver|
|Mészáros Roland|Frontend, PM|
|Nebl Gergő|Adatbázis, Backend|



