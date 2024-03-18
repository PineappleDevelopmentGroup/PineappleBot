package sh.miles.pineapplebot.old.structure;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Map;

public enum Modals {
    PLUGIN_COMMISSION("plugin-commission", "Plugin Commission",
            input("plugin-function", "What should the plugin do?", "Explain what it should do", 2, true, 1000),
            input("plugin-budget", "What is the budget?",
                    "Do you have a budget? If yes what is it. If no budget this blank.", 1, false, 100
            ),
            input("plugin-other", "Anything else we should know?", "Any other information you think we should know", 2,
                    false, 1000
            )
    ),
    SERVER_SETUP("server-setup", "Server Setup",
            input("server-type", "Server Type", "What type of server do you want us to setup?", 1, true, 500),
            input("setup-needed", "Setup Required", "What setup is needed?", 1, true, 1000),
            input("setup-info", "Setup Info", "How do you want things?", 2, true, 1000),
            input("setup-other", "Other Info", "Any other information you think we should know?", 2, false, 1000),
            input("setup-budget", "What is the budget?",
                    "Do you have a budget? If yes what is it. If no budget this blank.", 1, false, 100
            )
    ),
    PLUGIN_SETUP("plugin-setup", "Plugin Setup",
            input("setup-needed", "Setup Required", "What setup is needed and for what plugin?", 1, true, 1000),
            input("setup-info", "Setup Info",
                    "How do you want the end product to look? (This can include colors, message and message format)", 2,
                    true, 1000
            ),
            input("setup-other", "Other Info", "Any other information you think we should know, let us know here!", 2,
                    false, 1000
            ), input("setup-budget", "What is the budget?",
            "Do you have a budget? If yes what is it. If no budget this blank.", 1, false, 100
    )
    ),
    CREATOR_TITLE("creator-title", "Title", input("creator-title", "Title", 1, true, 1000),
            input("creator-title-url", "Title URL", 1, false, 1000)
    ),
    CREATOR_DESCRIPTION("creator-description", "Description",
            input("creator-description", "Description", 2, true, 1000)
    ),
    CREATOR_IMAGE("creator-image", "Image", input("creator-image", "Image Url", 1, true, 1000)),
    CREATOR_THUMBNAIL("creator-thumbnail", "Thumbnail",
            input("creator-thumbnail", "Thumbnail Image Url", 1, true, 1000)
    ),
    CREATOR_AUTHOR("creator-author", "Author", input("creator-author", "Author", 1, true, 100),
            input("creator-author-url", "Author URL", 1, false, 1000)
    ),
    CREATOR_FOOTER("creator-footer", "Footer", input("creator-footer", "Footer", 1, true, 512)),
    CREATOR_COLOR("creator-color", "Color", input("creator-color", "Color", 1, true, 10)),
    CREATOR_ADD_FIELD("creator-add-field", "Add Field", input("creator-add-field-name", "Field Name", 1, true, 500),
            input("creator-add-field-value", "Field Value", 1, true, 1000),
            input("creator-add-field-inline", "Field Inline", 1, true, 10)
    ),
    CREATOR_REMOVE_FIELD("creator-remove-field", "Remove Field",
            input("creator-remove-field", "Field Name", 1, true, 1000)
    ),
    CREATOR_PUBLISH_CHANNEL("creator-publish-channel", "Publish Channel",
            input("creator-publish-channel", "Channel ID", 1, true, 50)
    ),
    CREATOR_PUBLISH_NO_CHANNEL("creator-publish-channel", "Send Message, please set a channel id",
            input("creator-publish-channel", "Channel ID", 1, true, 50)
    ),
    CREATOR_ATTACH_COMMAND("creator-attach-command", "What text command should this embed attach to",
            input("creator-attached-command", "Text Command", 1, true, 100)
    );

    private final Modal modal;

    private static Map<Integer, TextInputStyle> INPUT_STYLES;

    Modals(String id, String name, TextInput... textInputs) {
        Modal.Builder builder = Modal.create(id, name);
        for (TextInput textInput : textInputs) {
            builder.addComponents(ActionRow.of(textInput));
        }
        this.modal = builder.build();
    }

    private static TextInputStyle getStyle(int id) {
        if (INPUT_STYLES == null) {
            INPUT_STYLES = Map.of(1, TextInputStyle.SHORT, 2,
                    TextInputStyle.PARAGRAPH
            );
        }
        return INPUT_STYLES.get(id);
    }

    public Modal getModal() {
        return modal;
    }

    private static TextInput input(String id, String label, String placeholder, int inputStyle, boolean required, int maxLength) {
        return TextInput.create(id, label, getStyle(inputStyle)).setPlaceholder(placeholder)
                .setMaxLength(maxLength).setRequired(required).build();
    }

    private static TextInput input(String id, String label, int inputStyle, boolean required, int maxLength) {
        return TextInput.create(id, label, getStyle(inputStyle)).setMaxLength(maxLength).setRequired(required)
                .build();
    }
}
