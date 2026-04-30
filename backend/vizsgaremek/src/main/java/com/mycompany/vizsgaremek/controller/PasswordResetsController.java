/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.service.PasswordResetsService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author neblgergo
 */
@Path("passwordReset")
public class PasswordResetsController {

    @Context
    private UriInfo context;
    private PasswordResetsService layer = new PasswordResetsService();
    private final JwtUtil jwt = new JwtUtil();

    /**
     * Creates a new instance of PasswordResetController
     */
    public PasswordResetsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.PasswordResetsController
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of
     * PasswordResetsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    // Elfelejtett jelszó email küldés
    @POST
    @Path("createPasswordReset")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPasswordResetController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        String email = bodyObject.has("email") ? bodyObject.getString("email") : null;

        JSONObject toReturn = layer.createPasswordResetService(email);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // Token validálás
    @GET
    @Path("getPasswordResetByToken")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPasswordResetByTokenController(@QueryParam("token") String token) {
        JSONObject toReturn = layer.getPasswordResetByTokenService(token);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // Jelszó visszaállítás token + új jelszó
    @PUT
    @Path("updatePasswordReset")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePasswordResetController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        String token = bodyObject.has("token") ? bodyObject.getString("token") : null;
        String newPassword = bodyObject.has("newPassword") ? bodyObject.getString("newPassword") : null;

        JSONObject toReturn = layer.updatePasswordResetService(token, newPassword);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeletePasswordReset")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeletePasswordResetController(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.softDeletePasswordResetService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // Jelszó visszaállítás token + új jelszó (transaction SP)
    @PUT
    @Path("resetPassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPasswordController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        String token = bodyObject.has("token") ? bodyObject.getString("token") : null;
        String newPassword = bodyObject.has("newPassword") ? bodyObject.getString("newPassword") : null;

        JSONObject toReturn = layer.resetPasswordService(token, newPassword);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("resetPasswordPage")
    @Produces(MediaType.TEXT_HTML)
    public Response resetPasswordPage(@QueryParam("token") String token) {
        String html = "<!doctype html>\n"
                + "<html lang=\"hu\">\n"
                + "  <head>\n"
                + "    <meta charset=\"UTF-8\" />\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
                + "    <title>Carcomps - Új jelszó</title>\n"
                + "    <link rel=\"icon\" href=\"https://media.discordapp.net/attachments/1403289167743553659/1428681580963958874/CarComps_Logo_C_white.png?ex=69caf028&is=69c99ea8&hm=3b2bcd15fa2b70c146a73705d54cea13c97118daea293a2b847d00d52d5221c9&=&format=webp&quality=lossless&width=434&height=475\" />\n"
                + "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css\" />\n"
                + "  </head>\n"
                + "  <style>\n"
                + "    * { margin: 0; padding: 0; box-sizing: border-box; font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; }\n"
                + "    body { background: #f0eded; min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; }\n"
                + "    .newpass-container { background: #fff; border-radius: 20px; box-shadow: 10px 10px 14px 0px rgba(0,0,0,0.1); width: 400px; overflow: hidden; }\n"
                + "    .header { background-color: #2b2b2b; border-top-right-radius: 15px; border-top-left-radius: 15px; height: 90px; display: flex; justify-content: center; align-items: center; }\n"
                + "    .header img { width: 65px; display: block; }\n"
                + "    .newpass-body { padding: 32px 36px 40px; display: flex; flex-direction: column; gap: 0; }\n"
                + "    .body-title { font-size: 17px; font-weight: 700; color: #2b2b2b; margin-bottom: 20px; text-align: center; }\n"
                + "    .field { margin-bottom: 28px; }\n"
                + "    .field-wrap { position: relative; padding-top: 18px; }\n"
                + "    .field-wrap label { position: absolute; top: 0; left: 0; font-size: 0.85rem; font-weight: 600; color: #2b2b2b; pointer-events: none; transition: 0.2s; }\n"
                + "    .field-wrap input { width: 100%; border: 0; border-bottom: 2px solid #2b2b2b; outline: 0; font-size: 1rem; color: #2b2b2b; padding: 4px 0; background: transparent; transition: border-color 0.2s; font-family: inherit; display: block; }\n"
                + "    .field-wrap input::placeholder { color: transparent; }\n"
                + "    .field-wrap input:placeholder-shown ~ label { font-size: 1rem; top: 22px; color: #999; font-weight: 400; }\n"
                + "    .field-wrap input:focus ~ label { top: 0; font-size: 0.85rem; color: #ff6600; font-weight: 700; }\n"
                + "    .field-wrap input:focus { padding-bottom: 4px; border-width: 3px; border-image: linear-gradient(to right, #ff6600, #d05604); border-image-slice: 1; }\n"
                + "    .field-wrap input:required, .field-wrap input:invalid { box-shadow: none; }\n"
                + "    .field-wrap input.error { border-color: #ff4444 !important; border-image: none !important; }\n"
                + "    .field-err { display: none; font-size: 11px; color: #ff4444; margin-top: 5px; padding-left: 4px; }\n"
                + "    .field-err.show { display: block; }\n"
                + "    .err-banner { display: none; align-items: center; gap: 8px; padding: 9px 12px; background: #fff5f5; border: 1px solid rgba(255,68,68,0.2); border-left: 3px solid #ff4444; border-radius: 8px; margin-bottom: 16px; }\n"
                + "    .err-banner.show { display: flex; }\n"
                + "    .err-banner i { color: #ff4444; font-size: 13px; flex-shrink: 0; }\n"
                + "    .err-banner p { font-size: 12px; color: #ff4444; margin: 0; }\n"
                + "    .confirmBtn { width: 100%; padding: 11px; background: #ff6600; color: #fff; border: none; border-radius: 20px; font-size: 14px; font-weight: 700; cursor: pointer; margin-top: 6px; display: flex; align-items: center; justify-content: center; gap: 8px; transition: background 0.2s; }\n"
                + "    .confirmBtn:hover { background: #cc4f00; }\n"
                + "    .confirmBtn:disabled { opacity: 0.5; cursor: not-allowed; background: #aaa; }\n"
                + "    .success-box { display: none; flex-direction: column; align-items: center; text-align: center; gap: 12px; padding: 8px 0 4px; }\n"
                + "    .success-box.show { display: flex; }\n"
                + "    .success-icon { width: 56px; height: 56px; border-radius: 50%; background: rgba(255,102,0,0.1); border: 2px solid rgba(255,102,0,0.25); display: flex; align-items: center; justify-content: center; font-size: 22px; color: #ff6600; }\n"
                + "    .success-box h3 { font-size: 15px; font-weight: 700; color: #2b2b2b; }\n"
                + "    .success-box p { font-size: 12px; color: #888; line-height: 1.5; }\n"
                + "    .btn-login { display: inline-flex; align-items: center; gap: 6px; padding: 10px 24px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 20px; font-size: 13px; font-weight: 700; margin-top: 4px; transition: background 0.2s; }\n"
                + "    .btn-login:hover { background: #cc4f00; }\n"
                + "    .token-loading { display: flex; flex-direction: column; align-items: center; gap: 14px; padding: 16px 0 8px; text-align: center; }\n"
                + "    .token-loading i { font-size: 28px; color: #ff6600; }\n"
                + "    .token-loading p { font-size: 13px; color: #888; }\n"
                + "    .token-invalid { display: none; flex-direction: column; align-items: center; text-align: center; gap: 12px; padding: 8px 0 4px; }\n"
                + "    .token-invalid.show { display: flex; }\n"
                + "    .invalid-icon { width: 56px; height: 56px; border-radius: 50%; background: rgba(255,68,68,0.08); border: 2px solid rgba(255,68,68,0.25); display: flex; align-items: center; justify-content: center; font-size: 22px; color: #ff4444; }\n"
                + "    .token-invalid h3 { font-size: 15px; font-weight: 700; color: #2b2b2b; }\n"
                + "    .token-invalid p { font-size: 12px; color: #888; line-height: 1.5; }\n"
                + "    .btn-back { display: inline-flex; align-items: center; gap: 6px; padding: 10px 24px; background: #2b2b2b; color: #fff; text-decoration: none; border-radius: 20px; font-size: 13px; font-weight: 700; margin-top: 4px; transition: background 0.2s; }\n"
                + "    .btn-back:hover { background: #444; }\n"
                + "  </style>\n"
                + "  <body>\n"
                + "    <div class=\"newpass-container\">\n"
                + "      <div class=\"header\">\n"
                + "        <img src=\"https://media.discordapp.net/attachments/1403289167743553659/1428681580963958874/CarComps_Logo_C_white.png?ex=69c3afe8&is=69c25e68&hm=2ff3a516bdf1f7d734eba2cfc41d72cceb3d13d95e36a331122ebab77b512313&=&format=webp&quality=lossless&width=434&height=475\" alt=\"CarComps\" />\n"
                + "      </div>\n"
                + "      <div class=\"newpass-body\">\n"
                + "        <div class=\"token-loading\" id=\"tokenLoading\">\n"
                + "          <i class=\"fa-solid fa-spinner fa-spin\"></i>\n"
                + "          <p>Link ellenőrzése...</p>\n"
                + "        </div>\n"
                + "        <div class=\"token-invalid\" id=\"tokenInvalid\">\n"
                + "          <div class=\"invalid-icon\"><i class=\"fa-solid fa-link-slash\"></i></div>\n"
                + "          <h3>Érvénytelen vagy lejárt link</h3>\n"
                + "          <p>Ez a jelszó-visszaállítási link már nem érvényes vagy lejárt.<br/>Kérj egy új linket a bejelentkezési oldalon.</p>\n"
                + "          <a href=\"https://carcomps.hu/login\" class=\"btn-back\"><i class=\"fa-solid fa-arrow-left\"></i> Vissza a bejelentkezéshez</a>\n"
                + "        </div>\n"
                + "        <div id=\"mainContent\" style=\"display:none\">\n"
                + "          <p class=\"body-title\">Új jelszó beállítása</p>\n"
                + "          <div class=\"err-banner\" id=\"errBanner\">\n"
                + "            <i class=\"fa-solid fa-circle-exclamation\"></i>\n"
                + "            <p id=\"errText\">Hiba történt.</p>\n"
                + "          </div>\n"
                + "          <div id=\"formSection\">\n"
                + "            <div class=\"field\">\n"
                + "              <div class=\"field-wrap\">\n"
                + "                <input type=\"password\" id=\"newpass\" placeholder=\"Új jelszó\" oninput=\"onPassInput()\" />\n"
                + "                <label for=\"newpass\">Új jelszó</label>\n"
                + "              </div>\n"
                + "              <div class=\"field-err\" id=\"passErr\">Min. 8 karakter, nagybetű, kisbetű, szám és speciális karakter szükséges</div>\n"
                + "            </div>\n"
                + "            <div class=\"field\">\n"
                + "              <div class=\"field-wrap\">\n"
                + "                <input type=\"password\" id=\"newpassre\" placeholder=\"Jelszó megerősítése\" oninput=\"onConfirmInput()\" />\n"
                + "                <label for=\"newpassre\">Jelszó megerősítése</label>\n"
                + "              </div>\n"
                + "              <div class=\"field-err\" id=\"confirmErr\">A két jelszó nem egyezik</div>\n"
                + "            </div>\n"
                + "            <button class=\"confirmBtn\" id=\"confirmBtn\" onclick=\"handleSubmit()\">\n"
                + "              <i class=\"fa-solid fa-lock\"></i> Megerősítés\n"
                + "            </button>\n"
                + "          </div>\n"
                + "          <div class=\"success-box\" id=\"successBox\">\n"
                + "            <div class=\"success-icon\"><i class=\"fa-solid fa-check\"></i></div>\n"
                + "            <h3>Jelszó megváltoztatva!</h3>\n"
                + "            <p>Sikeresen beállítottad az új jelszavadat.</p>\n"
                + "            <a href=\"https://carcomps.hu/login\" class=\"btn-login\"><i class=\"fa-solid fa-right-to-bracket\"></i> Bejelentkezés</a>\n"
                + "          </div>\n"
                + "        </div>\n"
                + "      </div>\n"
                + "    </div>\n"
                + "    <script>\n"
                + "      const BASE_URL = 'https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/';\n"
                + "      const token = new URLSearchParams(window.location.search).get('token');\n"
                + "      async function validateToken() {\n"
                + "        if (!token) { showInvalid(); return; }\n"
                + "        try {\n"
                + "          const res = await fetch(BASE_URL + 'passwordReset/getPasswordResetByToken?token=' + token);\n"
                + "          const data = await res.json();\n"
                + "          if (res.ok && data.statusCode === 200 && data.token) {\n"
                + "            document.getElementById('tokenLoading').style.display = 'none';\n"
                + "            document.getElementById('mainContent').style.display = 'block';\n"
                + "          } else { showInvalid(); }\n"
                + "        } catch { showInvalid(); }\n"
                + "      }\n"
                + "      function showInvalid() {\n"
                + "        document.getElementById('tokenLoading').style.display = 'none';\n"
                + "        document.getElementById('tokenInvalid').classList.add('show');\n"
                + "      }\n"
                + "      validateToken();\n"
                + "      function isValidPassword(v) {\n"
                + "        return v.length >= 8 && /[A-Z]/.test(v) && /[a-z]/.test(v) && /[0-9]/.test(v) && /[^A-Za-z0-9]/.test(v);\n"
                + "      }\n"
                + "      function onPassInput() {\n"
                + "        const v = document.getElementById('newpass').value;\n"
                + "        if (isValidPassword(v)) {\n"
                + "          document.getElementById('newpass').classList.remove('error');\n"
                + "          document.getElementById('passErr').classList.remove('show');\n"
                + "        }\n"
                + "      }\n"
                + "      function onConfirmInput() {\n"
                + "        const p = document.getElementById('newpass').value;\n"
                + "        const c = document.getElementById('newpassre').value;\n"
                + "        if (c && c === p) {\n"
                + "          document.getElementById('newpassre').classList.remove('error');\n"
                + "          document.getElementById('confirmErr').classList.remove('show');\n"
                + "        }\n"
                + "      }\n"
                + "      async function handleSubmit() {\n"
                + "        const pass = document.getElementById('newpass').value;\n"
                + "        const conf = document.getElementById('newpassre').value;\n"
                + "        let ok = true;\n"
                + "        if (!isValidPassword(pass)) {\n"
                + "          document.getElementById('newpass').classList.add('error');\n"
                + "          document.getElementById('passErr').classList.add('show');\n"
                + "          ok = false;\n"
                + "        }\n"
                + "        if (pass !== conf) {\n"
                + "          document.getElementById('newpassre').classList.add('error');\n"
                + "          document.getElementById('confirmErr').classList.add('show');\n"
                + "          ok = false;\n"
                + "        }\n"
                + "        if (!ok) return;\n"
                + "        const btn = document.getElementById('confirmBtn');\n"
                + "        btn.disabled = true;\n"
                + "        btn.innerHTML = '<i class=\"fa-solid fa-spinner fa-spin\"></i> Mentés...';\n"
                + "        try {\n"
                + "          const res = await fetch(BASE_URL + 'passwordReset/resetPassword', {\n"
                + "            method: 'PUT',\n"
                + "            headers: { 'Content-Type': 'application/json' },\n"
                + "            body: JSON.stringify({ token: token, newPassword: pass })\n"
                + "          });\n"
                + "          const data = await res.json();\n"
                + "          if (res.ok && (data.success || data.statusCode === 200)) {\n"
                + "            document.getElementById('formSection').style.display = 'none';\n"
                + "            document.getElementById('successBox').classList.add('show');\n"
                + "          } else {\n"
                + "            showErr(data.message || data.errors?.[0] || 'Hiba történt.');\n"
                + "            btn.disabled = false;\n"
                + "            btn.innerHTML = '<i class=\"fa-solid fa-lock\"></i> Megerősítés';\n"
                + "          }\n"
                + "        } catch {\n"
                + "          showErr('Nem sikerült kapcsolódni a szerverhez.');\n"
                + "          btn.disabled = false;\n"
                + "          btn.innerHTML = '<i class=\"fa-solid fa-lock\"></i> Megerősítés';\n"
                + "        }\n"
                + "      }\n"
                + "      function showErr(msg) {\n"
                + "        document.getElementById('errText').textContent = msg;\n"
                + "        document.getElementById('errBanner').classList.add('show');\n"
                + "      }\n"
                + "    </script>\n"
                + "  </body>\n"
                + "</html>";

        return Response.ok(html).build();
    }

}
