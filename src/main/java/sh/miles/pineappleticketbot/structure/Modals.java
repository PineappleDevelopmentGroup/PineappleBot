package sh.miles.pineappleticketbot.structure;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public enum Modals {
    PLUGIN_COMMISSION("plugin-commission",
            "Plugin Commission",
            TextInput.create("plugin-function",
                            "What should the plugin do?",
                            TextInputStyle.PARAGRAPH
                    ).setPlaceholder(
                            "Explain what it should do, if we need more detail we'll ask!")
                    .setMaxLength(1000).setRequired(true).build(),
            TextInput.create("plugin-budget",
                            "What is the budget?",
                            TextInputStyle.SHORT
                    ).setPlaceholder(
                            "Do you have a budget? If yes what is it. If no budget this blank.")
                    .setMaxLength(100).setRequired(false).build(),
            TextInput.create("plugin-other",
                            "Anything else we should know?",
                            TextInputStyle.PARAGRAPH
                    ).setPlaceholder(
                            "Any other information you think we should know, let us know here!")
                    .setRequired(false).setMaxLength(1000).build()
    ),
    SERVER_SETUP("server-setup", "Server Setup", TextInput
            .create("server-type", "Server Type", TextInputStyle.SHORT)
            .setPlaceholder(
                    "What type of server do you want us to setup? Include as much detail as you feel needed")
            .setMaxLength(500).setRequired(true).build(), TextInput
            .create("setup-needed", "Setup Required", TextInputStyle.PARAGRAPH)
            .setPlaceholder("What setup is needed?").setMaxLength(1000)
            .setRequired(true).build(),
            TextInput
                    .create("setup-info", "Setup Info", TextInputStyle.PARAGRAPH)
                    .setPlaceholder(
                            "How do you want things? What Colors do you want messages? As much info to help us create your vision")
                    .setMaxLength(1000).setRequired(true).build(),
            TextInput
                    .create("setup-other", "Other Info", TextInputStyle.PARAGRAPH)
                    .setPlaceholder(
                            "Any other information you think we should know, let us know here!")
                    .setRequired(false).setMaxLength(1000)
                    .build(),
            TextInput.create("setup-budget", "What is the budget?", TextInputStyle.SHORT)
                    .setPlaceholder("Do you have a budget? If yes what is it. If no budget this blank.")
                    .setRequired(false).setMaxLength(100).build()
    ),
    PLUGIN_SETUP("plugin-setup",
            "Plugin Setup",
            TextInput.create("setup-needed", "Setup Required", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("What setup is needed and for what plugin?").setRequired(true)
                    .setMaxLength(1000).build(),
            TextInput.create("setup-info", "Setup Info", TextInputStyle.PARAGRAPH)
                    .setPlaceholder(
                            "How do you want the end product to look? (This can include colors, message and message format)")
                    .setMaxLength(1000).setRequired(true).build(),
            TextInput.create("setup-other", "Other Info", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Any other information you think we should know, let us know here!")
                    .setMaxLength(1000).setRequired(false).build(),
            TextInput.create("setup-budget", "What is the budget?", TextInputStyle.SHORT)
                    .setPlaceholder("Do you have a budget? If yes what is it. If no budget this blank.")
                    .setMaxLength(100).setRequired(false).build()
    );

    private final Modal modal;

    Modals(String id, String name, TextInput... textInputs) {
        Modal.Builder builder = Modal.create(id, name);
        for (TextInput textInput : textInputs) {
            builder.addComponents(ActionRow.of(textInput));
        }
        this.modal = builder.build();
    }

    public Modal getModal() {
        return modal;
    }
}
