package io.baris.performance.lettucespringtest;

public interface GenieRepository {

    Genie getByIdJedis(String isbn);

    void createGenieJedis(Genie Genie);

    void createGenieLettuce(Genie Genie);

    Genie getByIdLettuce(String isbn);

}