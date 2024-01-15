package sh.miles.pineapplebot.structure;

import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Buttons {
    public static final Button TITLE = Button.primary("page-title", "Set Title");
    public static final Button DESCRIPTION = Button.primary("page-description", "Set Description");
    public static final Button COLOR = Button.primary("page-color", "Set Color");
    public static final Button THUMBNAIL = Button.primary("page-thumbnail", "Set Thumbnail");
    public static final Button IMAGE = Button.primary("page-image", "Set Image");
    public static final Button AUTHOR = Button.primary("page-author", "Set Author");
    public static final Button FOOTER = Button.primary("page-footer", "Set Footer");
    public static final Button ADD_FIELD = Button.primary("page-add-field", "Add Field");
    public static final Button REMOVE_FIELD = Button.danger("page-remove-field", "Remove Field");
    public static final Button SEND = Button.success("page-send", "Send");
    public static final Button PUBLISH_CHANNEL = Button.success("page-publish-channel", "Publish Channel");
}
