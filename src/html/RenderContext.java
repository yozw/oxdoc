package oxdoc.html;

import oxdoc.FileManager;

import static oxdoc.Utils.checkNotNull;

public class RenderContext {
  private final FileManager fileManager;

  public RenderContext(FileManager fileManager) {
    this.fileManager = checkNotNull(fileManager);
  }

  public FileManager getFileManager() {
    return fileManager;
  }
}
