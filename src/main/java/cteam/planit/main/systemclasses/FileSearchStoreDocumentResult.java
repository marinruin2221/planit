package cteam.planit.main.systemclasses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileSearchStoreDocumentResult {
  public List<FileSearchStoreDocument> documents;
}

