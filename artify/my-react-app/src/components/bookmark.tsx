import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar as faStarSolid } from "@fortawesome/free-solid-svg-icons";
import { faStar as faStarRegular } from "@fortawesome/free-regular-svg-icons";
// import { useState } from "react";
import { useAuth } from "../AuthContext";

const styles = {
  card: {
    backgroundColor: "#17438c",
    borderRadius: "100%",
    position: "absolute",
    top: "8px",
    right: "8px",
    fontSize: "22px",
    padding: "5px",
    cursor: "pointer",
  },
  header: {
    backgroundColor: "white",
    borderRadius: "100%",
    fontSize: "22px",
    padding: "5px",
    cursor: "pointer",
  },
};

type parType = "card" | "header";

function bookmark(props: { type: parType; id: string; img: string }) {
  const { userData, bookmarkedIds, toggleBookmark } = useAuth();
  const isBookmarked = bookmarkedIds.has(props.id);

  if (!userData) {
    return null;
  }

  const currStyle = styles[props.type];

  function handleClick(event: any) {
    event.stopPropagation();
    toggleBookmark(props.id, props.img);
  }
  return (
    <div>
      <FontAwesomeIcon
        icon={isBookmarked ? faStarSolid : faStarRegular}
        onClick={handleClick}
        style={
          {
            ...currStyle,
            color: isBookmarked ? "#f0ad4e" : "#999",
          } as React.CSSProperties
        }
      />
    </div>
  );
}

export default bookmark;
