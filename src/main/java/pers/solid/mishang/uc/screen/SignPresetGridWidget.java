package pers.solid.mishang.uc.screen;

import pers.solid.mishang.uc.shim.fabricmc.api.EnvType;
import pers.solid.mishang.uc.shim.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import pers.solid.mishang.uc.text.TextContext;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SignPresetGridWidget extends ContainerObjectSelectionList<SignPresetGridWidget.Entry> {
  /**
   * 决定着此元素在告示牌编辑界面中是否在显示，如果为 false，则 {@link #isMouseOver(double, double)} 始终为 false，这是为了避免当告示牌预设列表未显示时仍视为被悬浮导致无法操作告示牌编辑界面的问题。此字段仅存在于 1.20.1 中。
   */
  public boolean active = true;

  public SignPresetGridWidget(Minecraft minecraftClient, int width, int height, int top, int bottom, int itemHeight) {
    super(minecraftClient, width, height, top, bottom, itemHeight);
  }

  public static SignPresetGridWidget createAllWidgets(AbstractSignBlockEditScreen<?> screen, Minecraft minecraftClient, int height, int top, int bottom) {
    final SignPresetGridWidget gridWidget = new SignPresetGridWidget(minecraftClient, screen.width, height, top, bottom, 20);
    final List<Button> widgets = new ArrayList<>(3);
    SignPresets.streamValues().forEach(value -> {
      final Button widgetForPreset = createWidgetForPreset(screen, value);
      widgets.add(widgetForPreset);
      if (widgets.size() >= 3) {
        gridWidget.addEntry(new pers.solid.mishang.uc.screen.SignPresetGridWidget.Entry(List.copyOf(widgets)));
        widgets.clear();
      }
    });
    if (!widgets.isEmpty()) {
      gridWidget.addEntry(new pers.solid.mishang.uc.screen.SignPresetGridWidget.Entry(List.copyOf(widgets)));
    }
    return gridWidget;
  }

  public static Button createWidgetForPreset(AbstractSignBlockEditScreen<?> screen, SignPreset signPreset) {
    Component description = signPreset.description();
    final MutableComponent idText = Component.translatable("message.mishanguc.signPreset.list.id_info", signPreset.id()).withStyle(ChatFormatting.GRAY);
    if (description != null) {
      description = Component.empty().append(description).append(CommonComponents.NEW_LINE).append(idText);
    } else {
      description = idText;
    }
    return new Button.Builder(signPreset.name(), button -> {
      for (TextContext textContext : signPreset.textContexts()) {
        final TextContext newTextContext = textContext.clone();
        screen.textFieldListWidget.addTextField(-1, newTextContext, false);
      }
      final List<TextFieldListWidget.Entry> children = screen.textFieldListWidget.children();
      final int initialFocus = signPreset.initialFocus();
      if (initialFocus >= 0 && initialFocus < children.size()) {
        screen.setFocused(screen.textFieldListWidget);
        screen.textFieldListWidget.setFocusedAndSelected(children.get(initialFocus), false, false);
      }
      screen.rearrange();
    }).bounds(0, 0, 150, 20)
        .tooltip(Tooltip.create(description))
        .build();
  }

  @Override
  public int getRowWidth() {
    return 450;
  }

  @Override
  protected int getScrollbarPosition() {
    return width / 2 + 228;
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return this.active && super.isMouseOver(mouseX, mouseY);
  }

  public static class Entry extends ContainerObjectSelectionList.Entry<pers.solid.mishang.uc.screen.SignPresetGridWidget.Entry> {
    public final List<Button> buttons;

    public Entry(List<Button> buttons) {
      this.buttons = buttons;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
      return buttons;
    }

    @Override
    public List<? extends GuiEventListener> children() {
      return buttons;
    }

    @Override
    public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
      for (int i = 0, buttonsSize = buttons.size(); i < buttonsSize; i++) {
        Button button = buttons.get(i);
        button.setX(x + i * 150);
        button.setY(y);
        button.render(context, mouseX, mouseY, tickProgress);
      }
    }
  }
}
