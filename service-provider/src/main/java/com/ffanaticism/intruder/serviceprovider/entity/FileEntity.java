package com.ffanaticism.intruder.serviceprovider.entity;

import com.ffanaticism.intruder.serviceprovider.util.FileUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.data.annotation.Transient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class FileEntity extends StreamedEntity {
    public static final String AMOUNT_OF_DOWNLOADS_CHARACTERISTIC = "amountOfDownloads";

    private List<Characteristic> characteristics;

    @Transient
    private File file;

    public FileEntity(File file, List<Characteristic> characteristics, InputStream inputStream, InputStream coverStream, String name, String url, boolean isDirect) {
        super(inputStream, coverStream, name, url, isDirect);
        this.file = file;
        this.characteristics = characteristics;
    }

    public Characteristic getCharacteristic(String name) {
        Characteristic characteristic;
        if (characteristics != null) {
            characteristic = characteristics.stream()
                    .filter(e -> e.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        } else {
            characteristic = null;
        }

        return characteristic;
    }

    public void updateCharacteristic(String name, String value) {
        var contains = false;
        if (characteristics == null) {
            characteristics = new ArrayList<>();
        }
        for (var characteristic : characteristics) {
            if (characteristic.getName().equals(name)) {
                characteristic.setValue(value);
                contains = true;
                break;
            }
        }

        if (!contains) {
            characteristics.add(new Characteristic(false, name, value));
        }
    }

    public void setFile(File file) throws IOException {
        this.file = file;
        this.setInputStream(FileUtils.openInputStream(file));
    }

    public void deleteFile() {
        if (this.getFile() != null) {
            if (this.getInputStream() != null) {
                try {
                    this.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileUtil.delete(this.getFile());
        }
    }
}
