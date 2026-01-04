
export const Alert = (props) => {
  const { variant, children } = props;

  let backgroundColor = undefined;
  let color = undefined;

  if (variant === 'success') {
    backgroundColor = 'rgb(237, 247, 237)';
    color = 'rgb(30, 70, 32)';
  }

  if (variant === 'error') {
    backgroundColor = 'rgb(253, 237, 237)';
    color = 'rgb(95, 33, 32)';
  }

  return (
    <div
      style={{
        backgroundColor,
        color,
        padding: '1rem',
        whiteSpace: 'pre-wrap',
      }}
    >
      {children}
    </div>
  );
};