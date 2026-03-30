package br.com.erudio.file.exporter.factory;

import br.com.erudio.exception.BadRequestException;
import br.com.erudio.file.exporter.contract.FileExporter;
import br.com.erudio.file.exporter.impl.CsvExporter;
import br.com.erudio.file.exporter.impl.XlsxExporter;
import br.com.erudio.file.exporter.media.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);
    private final ApplicationContext context;

    public FileExporterFactory(ApplicationContext context) {
        this.context = context;
    }

    public FileExporter getExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) return context.getBean(XlsxExporter.class);

        else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) return context.getBean(CsvExporter.class);

        else throw new BadRequestException("Invalid file format!");
    }
}
