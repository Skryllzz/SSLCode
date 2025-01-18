package com.zenyte.api.client.webhook.model;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Corey
 * @since 06/04/2020
 */
public class EmbedObject {
    private static final DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private List<Field> fields;
    private String title;
    private String description;
    private String url;
    private int color;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private String timestamp;
    private Author author;


    EmbedObject(final List<Field> fields, final String title, final String description, final String url, final int color, final Footer footer, final Thumbnail thumbnail, final Image image, final String timestamp, final Author author) {
        this.fields = fields;
        this.title = title;
        this.description = description;
        this.url = url;
        this.color = color;
        this.footer = footer;
        this.thumbnail = thumbnail;
        this.image = image;
        this.timestamp = timestamp;
        this.author = author;
    }

    @NotNull
    public static EmbedObject.EmbedObjectBuilder builder() {
        return new EmbedObject.EmbedObjectBuilder();
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public void setFields(final List<Field> fields) {
        this.fields = fields;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(final int color) {
        this.color = color;
    }

    public Footer getFooter() {
        return this.footer;
    }

    public void setFooter(final Footer footer) {
        this.footer = footer;
    }

    public Thumbnail getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(final Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(final Image image) {
        this.image = image;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(final Author author) {
        this.author = author;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof EmbedObject other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getColor() != other.getColor()) return false;
        final Object this$fields = this.getFields();
        final Object other$fields = other.getFields();
        if (!Objects.equals(this$fields, other$fields)) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (!Objects.equals(this$title, other$title)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (!Objects.equals(this$description, other$description)) return false;
        final Object this$url = this.getUrl();
        final Object other$url = other.getUrl();
        if (!Objects.equals(this$url, other$url)) return false;
        final Object this$footer = this.getFooter();
        final Object other$footer = other.getFooter();
        if (!Objects.equals(this$footer, other$footer)) return false;
        final Object this$thumbnail = this.getThumbnail();
        final Object other$thumbnail = other.getThumbnail();
        if (!Objects.equals(this$thumbnail, other$thumbnail)) return false;
        final Object this$image = this.getImage();
        final Object other$image = other.getImage();
        if (!Objects.equals(this$image, other$image)) return false;
        final Object this$timestamp = this.getTimestamp();
        final Object other$timestamp = other.getTimestamp();
        if (!Objects.equals(this$timestamp, other$timestamp)) return false;
        final Object this$author = this.getAuthor();
        final Object other$author = other.getAuthor();
        return Objects.equals(this$author, other$author);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof EmbedObject;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getColor();
        final Object $fields = this.getFields();
        result = result * PRIME + ($fields == null ? 43 : $fields.hashCode());
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $url = this.getUrl();
        result = result * PRIME + ($url == null ? 43 : $url.hashCode());
        final Object $footer = this.getFooter();
        result = result * PRIME + ($footer == null ? 43 : $footer.hashCode());
        final Object $thumbnail = this.getThumbnail();
        result = result * PRIME + ($thumbnail == null ? 43 : $thumbnail.hashCode());
        final Object $image = this.getImage();
        result = result * PRIME + ($image == null ? 43 : $image.hashCode());
        final Object $timestamp = this.getTimestamp();
        result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
        final Object $author = this.getAuthor();
        result = result * PRIME + ($author == null ? 43 : $author.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "EmbedObject(fields=" + this.getFields() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", url=" + this.getUrl() + ", color=" + this.getColor() + ", footer=" + this.getFooter() + ", thumbnail=" + this.getThumbnail() + ", image=" + this.getImage() + ", timestamp=" + this.getTimestamp() + ", author=" + this.getAuthor() + ")";
    }

    public static class EmbedObjectBuilder {
        private List<Field> fields;
        private String title;
        private String description;
        private String url;
        private int color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private String timestamp;
        private Author author;

        EmbedObjectBuilder() {
        }

        public EmbedObjectBuilder field(String name, String value, boolean inline) {
            if (this.fields == null) {
                this.fields = new ArrayList<>();
            }
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        public EmbedObjectBuilder timestamp(final Date date) {
            this.timestamp = timestampFormat.format(date);
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder fields(final List<Field> fields) {
            this.fields = fields;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder title(final String title) {
            this.title = title;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder description(final String description) {
            this.description = description;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder url(final String url) {
            this.url = url;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder color(final int color) {
            this.color = color;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder footer(final Footer footer) {
            this.footer = footer;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder thumbnail(final Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder image(final Image image) {
            this.image = image;
            return this;
        }

        @NotNull
        public EmbedObject.EmbedObjectBuilder author(final Author author) {
            this.author = author;
            return this;
        }

        @NotNull
        public EmbedObject build() {
            return new EmbedObject(this.fields, this.title, this.description, this.url, this.color, this.footer, this.thumbnail, this.image, this.timestamp, this.author);
        }

        @NotNull
        @Override
        public String toString() {
            return "EmbedObject.EmbedObjectBuilder(fields=" + this.fields + ", title=" + this.title + ", description=" + this.description + ", url=" + this.url + ", color=" + this.color + ", footer=" + this.footer + ", thumbnail=" + this.thumbnail + ", image=" + this.image + ", timestamp=" + this.timestamp + ", author=" + this.author + ")";
        }
    }

    public static class Footer {
        private final String text;
        @SerializedName("icon_url")
        private final String iconUrl;

        public Footer(final String text, final String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }

        public String getText() {
            return this.text;
        }

        public String getIconUrl() {
            return this.iconUrl;
        }

        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) return true;
            if (!(o instanceof Footer other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$text = this.getText();
            final Object other$text = other.getText();
            if (!Objects.equals(this$text, other$text)) return false;
            final Object this$iconUrl = this.getIconUrl();
            final Object other$iconUrl = other.getIconUrl();
            return Objects.equals(this$iconUrl, other$iconUrl);
        }

        protected boolean canEqual(@Nullable final Object other) {
            return other instanceof EmbedObject.Footer;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $text = this.getText();
            result = result * PRIME + ($text == null ? 43 : $text.hashCode());
            final Object $iconUrl = this.getIconUrl();
            result = result * PRIME + ($iconUrl == null ? 43 : $iconUrl.hashCode());
            return result;
        }

        @NotNull
        @Override
        public String toString() {
            return "EmbedObject.Footer(text=" + this.getText() + ", iconUrl=" + this.getIconUrl() + ")";
        }
    }

    public static class Thumbnail {
        private final String url;

        public Thumbnail(final String url) {
            this.url = url;
        }

        public String getUrl() {
            return this.url;
        }

        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) return true;
            if (!(o instanceof Thumbnail other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            return Objects.equals(this$url, other$url);
        }

        protected boolean canEqual(@Nullable final Object other) {
            return other instanceof EmbedObject.Thumbnail;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            return result;
        }

        @NotNull
        @Override
        public String toString() {
            return "EmbedObject.Thumbnail(url=" + this.getUrl() + ")";
        }
    }

    public static class Image {
        private final String url;

        public Image(final String url) {
            this.url = url;
        }

        public String getUrl() {
            return this.url;
        }

        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) return true;
            if (!(o instanceof Image other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            return Objects.equals(this$url, other$url);
        }

        protected boolean canEqual(@Nullable final Object other) {
            return other instanceof EmbedObject.Image;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            return result;
        }

        @NotNull
        @Override
        public String toString() {
            return "EmbedObject.Image(url=" + this.getUrl() + ")";
        }
    }

    public static class Author {
        private final String name;
        private final String url;
        @SerializedName("icon_url")
        private final String iconUrl;

        public Author(final String name, final String url, final String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }

        public String getName() {
            return this.name;
        }

        public String getUrl() {
            return this.url;
        }

        public String getIconUrl() {
            return this.iconUrl;
        }

        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) return true;
            if (!(o instanceof Author other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$name = this.getName();
            final Object other$name = other.getName();
            if (!Objects.equals(this$name, other$name)) return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (!Objects.equals(this$url, other$url)) return false;
            final Object this$iconUrl = this.getIconUrl();
            final Object other$iconUrl = other.getIconUrl();
            return Objects.equals(this$iconUrl, other$iconUrl);
        }

        protected boolean canEqual(@Nullable final Object other) {
            return other instanceof EmbedObject.Author;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $name = this.getName();
            result = result * PRIME + ($name == null ? 43 : $name.hashCode());
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            final Object $iconUrl = this.getIconUrl();
            result = result * PRIME + ($iconUrl == null ? 43 : $iconUrl.hashCode());
            return result;
        }

        @NotNull
        @Override
        public String toString() {
            return "EmbedObject.Author(name=" + this.getName() + ", url=" + this.getUrl() + ", iconUrl=" + this.getIconUrl() + ")";
        }
    }

    public static class Field {
        private final String name;
        private final String value;
        private final boolean inline;

        public Field(final String name, final String value, final boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isInline() {
            return this.inline;
        }

        @Override
        public boolean equals(@Nullable final Object o) {
            if (o == this) return true;
            if (!(o instanceof Field other)) return false;
            if (!other.canEqual(this)) return false;
            if (this.isInline() != other.isInline()) return false;
            final Object this$name = this.getName();
            final Object other$name = other.getName();
            if (!Objects.equals(this$name, other$name)) return false;
            final Object this$value = this.getValue();
            final Object other$value = other.getValue();
            return Objects.equals(this$value, other$value);
        }

        protected boolean canEqual(@Nullable final Object other) {
            return other instanceof EmbedObject.Field;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + (this.isInline() ? 79 : 97);
            final Object $name = this.getName();
            result = result * PRIME + ($name == null ? 43 : $name.hashCode());
            final Object $value = this.getValue();
            result = result * PRIME + ($value == null ? 43 : $value.hashCode());
            return result;
        }

        @NotNull
        @Override
        public String toString() {
            return "EmbedObject.Field(name=" + this.getName() + ", value=" + this.getValue() + ", inline=" + this.isInline() + ")";
        }
    }
}
