package io.baris.performance.lettucespringtest;

public interface GenieRepository {

    Genie getByIdJedis(String isbn);

    Genie createGenieJedis(Genie Genie);

    Genie createGenieLettuce(Genie Genie);

    Genie getByIdLettuce(String isbn);

}