import { useState } from "react";

function ListingImage({
  imageUrl,
  alt,
  placeholder,
  placeholderClassName,
}) {
  const [imageFailed, setImageFailed] =
    useState(false);

  if (!imageUrl || imageFailed) {
    return (
      <div className={placeholderClassName}>
        {placeholder}
      </div>
    );
  }

  return (
    <img
      src={imageUrl}
      alt={alt}
      onError={() =>
        setImageFailed(true)
      }
    />
  );
}

export default ListingImage;