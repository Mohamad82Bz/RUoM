package me.Mohamad82.RUoM.translators.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import me.Mohamad82.RUoM.Ruom;
import me.Mohamad82.RUoM.translators.skin.exceptions.MineSkinAPIException;
import me.Mohamad82.RUoM.translators.skin.exceptions.SkinParseException;
import me.Mohamad82.RUoM.translators.skin.exceptions.SkinRequestException;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MineSkinAPI {

    private final Queue<UUID> queue = new LinkedList<>();
    private final Map<UUID, AtomicInteger> fails = new HashMap<>();

    protected MinecraftSkin getSkin(String url, SkinType skinType) throws MineSkinAPIException, SkinParseException {
        UUID methodUUID = UUID.randomUUID();
        queue.add(methodUUID);

        fails.putIfAbsent(methodUUID, new AtomicInteger());

        if (fails.get(methodUUID).get() >= 5) {
            queue.remove(methodUUID);
            throw new MineSkinAPIException();
        }

        try {
            if (queue.element().equals(methodUUID)) {
                String skinVariant = skinType != null && (skinType.equals(SkinType.NORMAL) || skinType.equals(SkinType.SLIM)) ? "&variant=" + skinType : "";

                try {
                    final String output = queryURL("url=" + URLEncoder.encode(url, "UTF-8") + skinVariant);
                    if (output.isEmpty()) //api time out
                        throw new MineSkinAPIException();

                    final JsonObject obj = new JsonParser().parse(output).getAsJsonObject();

                    if (obj.has("data")) {
                        final JsonObject dta = obj.get("data").getAsJsonObject();

                        if (dta.has("texture")) {
                            final JsonObject tex = dta.get("texture").getAsJsonObject();

                            return new MinecraftSkin(tex.get("value").getAsString(), tex.get("signature").getAsString());
                        }
                    } else if (obj.has("error")) {
                        final String errResp = obj.get("error").getAsString();

                        // If we send to many request, go sleep and try again.
                        if (errResp.equals("Too many requests")) {
                            fails.get(methodUUID).incrementAndGet();

                            // If "Too many requests"
                            if (obj.has("delay")) {
                                TimeUnit.SECONDS.sleep(obj.get("delay").getAsInt());

                                return getSkin(url, skinType); // try again after nextRequest
                            } else if (obj.has("nextRequest")) {
                                final long nextRequestMilS = (long) ((obj.get("nextRequest").getAsDouble() * 1000) - System.currentTimeMillis());

                                if (nextRequestMilS > 0)
                                    TimeUnit.MILLISECONDS.sleep(nextRequestMilS);

                                return getSkin(url, skinType); // try again after nextRequest
                            } else {
                                TimeUnit.SECONDS.sleep(2);

                                return getSkin(url, skinType); // try again after nextRequest
                            }
                        }

                        if (errResp.equals("Failed to generate skin data") || errResp.equals("Failed to change skin")) {
                            Ruom.debug("[ERROR] MS " + errResp + ", trying again... ");
                            TimeUnit.SECONDS.sleep(5);

                            return getSkin(url, skinType); // try again
                        } else if (errResp.equals("No accounts available")) {
                            Ruom.debug("[ERROR] " + errResp + " for: " + url);

                            throw new MineSkinAPIException();
                        }

                        Ruom.debug("[ERROR] MS:reason: " + errResp);
                        throw new SkinParseException();
                    }
                } catch (MineSkinAPIException e) {
                    throw new MineSkinAPIException();
                } catch (SkinParseException e) {
                    throw new SkinParseException();
                } catch (IOException e) {
                    Ruom.debug("[ERROR] MS API Failure IOException (connection/disk): (" + url + ") " + e.getLocalizedMessage());
                } catch (JsonSyntaxException e) {
                    Ruom.debug("[ERROR] MS API Failure JsonSyntaxException (encoding): (" + url + ") " + e.getLocalizedMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // throw exception after all tries have failed
                Ruom.debug("[ERROR] MS:could not generate skin url: " + url);
                throw new MineSkinAPIException();
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new MineSkinAPIException();
                }

                return getSkin(url, skinType);
            }
        } finally {
            queue.remove(methodUUID);
        }
    }

    private String queryURL(String query) throws IOException {
        for (int i = 0; i < 3; i++) { // try 3 times, if server not responding
            try {
                HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.mineskin.org/generate/url/").openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("Content-length", String.valueOf(query.length()));
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("User-Agent", "SkinsRestorer");
                con.setConnectTimeout(90000);
                con.setReadTimeout(90000);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestProperty("Authorization", "f4230c46316bbf55555e35d2e49b57b3b36baa870221af75cd7ba6d9af252de8");

                DataOutputStream output = new DataOutputStream(con.getOutputStream());
                output.writeBytes(query);
                output.close();
                StringBuilder outStr = new StringBuilder();
                InputStream is;

                try {
                    is = con.getInputStream();
                } catch (Exception e) {
                    is = con.getErrorStream();
                }

                DataInputStream input = new DataInputStream(is);
                for (int c = input.read(); c != -1; c = input.read())
                    outStr.append((char) c);

                input.close();
                return outStr.toString();
            } catch (Exception ignored) {
            }
        }

        return "";
    }

    public enum SkinType {
        NORMAL,
        SLIM
    }

}
